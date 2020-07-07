package cn.stream2000.railgunmq.broker.server;

import cn.stream2000.railgunmq.broker.BrokerMessageHandler;
import cn.stream2000.railgunmq.broker.strategy.ConsumerStrategy;
import cn.stream2000.railgunmq.broker.strategy.ProducerStrategy;
import cn.stream2000.railgunmq.core.ConsumerMessage;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.netty.codec.MessageStrategyProtobufDecoder;
import cn.stream2000.railgunmq.netty.codec.ProtoRouter;
import cn.stream2000.railgunmq.netty.codec.ProtobufEncoder;
import cn.stream2000.railgunmq.netty.codec.RouterInitializer;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
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
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RailgunMQBrokerServer extends BrokerParallelServer {

    private final ThreadFactory threadBossFactory =
        new ThreadFactoryBuilder().setNameFormat("RailgunMQBroker[BossSelector]-%d").setDaemon(true)
            .build();

    private final ThreadFactory threadWorkerFactory =
        new ThreadFactoryBuilder().setNameFormat("RailgunMQBroker[WorkerSelector]-%d")
            .setDaemon(true)
            .build();

    private final ProtoRouter router = RouterInitializer.initialize();
    @Value("${netty.port}")
    private Integer port;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private EventLoopGroup boss;
    private EventLoopGroup workers;
    private ServerBootstrap bootstrap;

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
            .localAddress(new InetSocketAddress(port)).childHandler(new ServerInitializer());
        super.init();


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
    @PostConstruct
    public void start() {
        this.init();
        try {
            super.start();
            this.bootstrap.bind().sync();
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
            router.registerHandler(ProducerMessage.PubMessageRequest.getDefaultInstance(),
                new ProducerStrategy.PublishMessageStrategy());
            router.registerHandler(ProducerMessage.SetChannelName.getDefaultInstance(),
                    new ProducerStrategy.SetNameStrategy());
            router.registerHandler(ProducerMessage.Disconnect.getDefaultInstance(),
                    new ProducerStrategy.DisconnectStrategy());
            router.registerHandler(ConsumerMessage.SubMessageRequest.getDefaultInstance(),
                    new ConsumerStrategy.SubscribeMessageStrategy());
            router.registerHandler(ConsumerMessage.SendMessageAck.getDefaultInstance(),
                    new ConsumerStrategy.AckMessageStrategy());
            ch.pipeline().addLast(handler);
        }
    }
}
