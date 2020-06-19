package cn.stream2000.railgunmq.broker.server;

import cn.stream2000.railgunmq.broker.BrokerMessageHandler;
import cn.stream2000.railgunmq.broker.MessageDispatcher;
import cn.stream2000.railgunmq.broker.PubMessageTaskFactory;
import cn.stream2000.railgunmq.broker.strategy.ProducerStrategy;
import cn.stream2000.railgunmq.common.config.StoreConfig;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.netty.codec.MessageStrategyProtobufDecoder;
import cn.stream2000.railgunmq.netty.codec.ProtoRouter;
import cn.stream2000.railgunmq.netty.codec.ProtobufEncoder;
import cn.stream2000.railgunmq.netty.codec.RouterInitializer;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;
import cn.stream2000.railgunmq.store.db.RDB;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RailgunMQBrokerServer extends BrokerParallelServer {

    private final ThreadFactory threadBossFactory =
        new ThreadFactoryBuilder().setNameFormat("RailgunMQBroker[BossSelector]-%d").setDaemon(true)
            .build();

    private final ThreadFactory threadWorkerFactory =
        new ThreadFactoryBuilder().setNameFormat("RailgunMQBroker[WorkerSelector]-%d")
            .setDaemon(true)
            .build();

    private final ProtoRouter router = RouterInitializer.initialize();
    private String addr = "127.0.0.1";
    private int port = 8080;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private EventLoopGroup boss;
    private EventLoopGroup workers;
    private ServerBootstrap bootstrap;
    private MessageDispatcher messageDispatcher;

    public RailgunMQBrokerServer() {

    }

    public RailgunMQBrokerServer(int port) {
        this.port = port;
    }

    public RailgunMQBrokerServer(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }

    public static void main(String[] args) {
        RailgunMQBrokerServer server = new RailgunMQBrokerServer(9999);
        server.init();
        server.start();
    }

    @Override
    public void init() {
        boss = new NioEventLoopGroup(1, threadBossFactory);

        workers = new NioEventLoopGroup(parallel, threadWorkerFactory);

        defaultEventExecutorGroup = new DefaultEventExecutorGroup(1);
        bootstrap = new ServerBootstrap();
        bootstrap.group(boss, workers).channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.SO_KEEPALIVE, false)
            .childOption(ChannelOption.TCP_NODELAY, true).handler(new LoggingHandler(LogLevel.INFO))
            .localAddress(new InetSocketAddress(addr, port)).childHandler(new ServerInitializer());

        super.init();

        // init the core services
        StoreConfig storeConfig = new StoreConfig();
        RDB db = new RDB(storeConfig);
        db.init();

        OfflineMessageStore offlineMessageStore = new OfflineMessageStore(db);
        PersistenceMessageStore persistenceMessageStore = new PersistenceMessageStore(db);
        int pollNum = Runtime.getRuntime().availableProcessors() * 2;

        MessageDispatcher messageDispatcher = new MessageDispatcher(pollNum, offlineMessageStore);
        PubMessageTaskFactory.getInstance()
            .SetUpPubMessageTaskFactory(offlineMessageStore, persistenceMessageStore,
                messageDispatcher);
        this.messageDispatcher  = messageDispatcher;
    }

    @Override
    public void shutdown() {
        try {
            super.shutdown();
            boss.shutdownGracefully();
            workers.shutdownGracefully();
            defaultEventExecutorGroup.shutdownGracefully();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("RailgunMQBrokerServer shutdown exception!");
        }
    }

    @Override
    public void start() {
        try {
            ChannelFuture sync = this.bootstrap.bind().sync();
            super.start();
            messageDispatcher.start();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            Logger.getLogger(RailgunMQBrokerServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class ServerInitializer extends ChannelInitializer<SocketChannel> {

        ProtobufEncoder encoder = new ProtobufEncoder(router);
        BrokerMessageHandler handler = new BrokerMessageHandler();

        @Override
        protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(encoder);
            ch.pipeline().addLast(new MessageStrategyProtobufDecoder(router));
            //TODO add more handlers to handler different kinds of messages
            router.registerHandler(ProducerMessage.PubMessageRequest.getDefaultInstance(),
                new ProducerStrategy.PublishMessageStrategy());
            ch.pipeline().addLast(handler);
        }
    }
}