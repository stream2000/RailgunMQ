package cn.stream2000.railgunmq.consumer;

import cn.stream2000.railgunmq.core.Connection;
import cn.stream2000.railgunmq.core.ConsumerMessage;
import cn.stream2000.railgunmq.core.SemaphoreCache;
import cn.stream2000.railgunmq.netty.MessageStrategy;
import cn.stream2000.railgunmq.netty.codec.MessageStrategyProtobufDecoder;
import cn.stream2000.railgunmq.netty.codec.ProtoRouter;
import cn.stream2000.railgunmq.netty.codec.ProtobufEncoder;
import cn.stream2000.railgunmq.netty.codec.RouterInitializer;
import com.google.protobuf.ByteString;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@ChannelHandler.Sharable
public class RailgunMQConsumer {
    private final String host;
    private final int port;
    private Connection connection;
    public BlockingQueue<ConsumerMessage.SubMessageAck> blockingQueue;
    public String channelId;

    //在初始化时指明IP和端口
    RailgunMQConsumer(String host, int port) throws InterruptedException {
        this.host = host;
        this.port = port;
        channelId = "";
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap =
                new Bootstrap().group(group).channel(NioSocketChannel.class)
                        .handler(new ConsumerInitializer());
        Channel channel = bootstrap.connect(host, port).sync().channel();
        SemaphoreCache.acquire("client init");
        blockingQueue = new LinkedBlockingDeque<>();
    }

    public RailgunMQConsumer(String host, int port, String connectionName) throws InterruptedException {
        this.host = host;
        this.port = port;
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap =
                new Bootstrap().group(group).channel(NioSocketChannel.class)
                        .handler(new ConsumerInitializer());
        Channel channel = bootstrap.connect(host, port).sync().channel();
        SemaphoreCache.acquire("client init");
        blockingQueue = new LinkedBlockingDeque<>();
        setChannelName(connectionName);
    }

    public void setChannelName(String channelName) {
        sendSubRequest("Rename", channelName);
    }

    public void disconnect() {
        //发送关闭channel的消息
        sendSubRequest("disconnect", "断开连接");
        connection.getChannel().disconnect();
    }

    //获取单个ACK
    public ConsumerMessage.SubMessageAck getAck() throws InterruptedException {
        return this.blockingQueue.poll();
    }

    //在限定时间内取一些ACK
    public List<ConsumerMessage.SubMessageAck> getAcks(long maxTime) {
        List<ConsumerMessage.SubMessageAck> acks = new ArrayList<>();
        long StartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - StartTime <= maxTime) {
            ConsumerMessage.SubMessageAck ack = blockingQueue.poll();
            if (ack != null) {
                acks.add(ack);
            }
        }
        return acks;
    }

    public int getAckNum() {
        return this.blockingQueue.size();
    }

    public void sendSubAck(String topic, String msgId, boolean isSuccess) {
        try {
            ConsumerMessage.SendMessageAck ack =
                    ConsumerMessage.SendMessageAck.newBuilder().setChannelId(channelId)
                            .setTopic(topic)
                            .setMsgId(msgId)
                            .setIsSuccess(isSuccess).build();
            connection.getChannel().writeAndFlush(ack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSubRequest(String topic, String message) {
        try {
            ConsumerMessage.SubMessageRequest request =
                    ConsumerMessage.SubMessageRequest.newBuilder().setChannelId(channelId)
                            .setType(ConsumerMessage.SubMessageRequest.payload_type.Text)
                            .setData(ByteString.copyFromUtf8(message))
                            .setTopic(topic).build();
            connection.getChannel().writeAndFlush(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register() {
        EventLoopGroup group = new NioEventLoopGroup(8);
        try {
            Bootstrap bootstrap =
                    new Bootstrap().group(group).channel(NioSocketChannel.class)
                            .handler(new ConsumerInitializer());

            final Channel channel = bootstrap.connect(host, port).sync().channel();
            SemaphoreCache.acquire("consumer init");
            ConsumerMessage.SubMessageRequest request = ConsumerMessage.SubMessageRequest.newBuilder().setChannelId(channelId).build();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public class SubMessageResponseStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            ConsumerMessage.SubMessageAck ack = (ConsumerMessage.SubMessageAck) message;
            System.out.println("订阅是否成功：  " + ack.getError());
            System.out.println(ack.getErrorMessage());
            blockingQueue.add(ack);
        }
    }

    public class ConsumerInitStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            channelId = ((ConsumerMessage.CreateChannelResponse) message).getChannelId();
            SemaphoreCache.release("client init");
        }
    }

    public class ConsumerInitializer extends ChannelInitializer<SocketChannel> {

        ProtobufEncoder encoder = new ProtobufEncoder(RouterInitializer.initialize());
        ProtoRouter router = RouterInitializer.initialize();
        MessageConsumerHandler handler = new MessageConsumerHandler();

        @Override
        protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(encoder);
            ch.pipeline().addLast(new MessageStrategyProtobufDecoder(router));
            router.registerHandler(ConsumerMessage.SubMessageAck.getDefaultInstance(),
                    new SubMessageResponseStrategy());
            router.registerHandler(ConsumerMessage.CreateChannelResponse.getDefaultInstance(),
                    new ConsumerInitStrategy());
            ch.pipeline().addLast(handler);
        }
    }

}

