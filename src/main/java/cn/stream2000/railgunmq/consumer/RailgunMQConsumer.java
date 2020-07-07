package cn.stream2000.railgunmq.consumer;

import cn.stream2000.railgunmq.core.ConsumerMessage;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.netty.MessageStrategy;
import cn.stream2000.railgunmq.netty.codec.MessageStrategyProtobufDecoder;
import cn.stream2000.railgunmq.netty.codec.ProtoRouter;
import cn.stream2000.railgunmq.netty.codec.ProtobufEncoder;
import cn.stream2000.railgunmq.netty.codec.RouterInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class RailgunMQConsumer {

    private final Channel channel;
    public BlockingQueue<ConsumerMessage.SubMessageAck> blockingQueue;
    public String channelId;


    public RailgunMQConsumer(String host, int port, String topic, String connectionName)
        throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap =
            new Bootstrap().group(group).channel(NioSocketChannel.class)
                .handler(new ConsumerInitializer());
        blockingQueue = new LinkedBlockingDeque<>();
        channel = bootstrap.connect(host, port).sync().channel();
        channelId = channel.id().asLongText();
        ConsumerMessage.SubMessageRequest request = ConsumerMessage.SubMessageRequest
            .newBuilder()
            .setTopic(topic)
            .setName(connectionName)
            .build();
        channel.writeAndFlush(request);
    }

    //获取单个ACK
    public ConsumerMessage.SubMessageAck getAck() throws InterruptedException {
        return this.blockingQueue.take();
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
        ConsumerMessage.SendMessageAck ack =
            ConsumerMessage.SendMessageAck.newBuilder().setChannelId(channelId)
                .setTopic(topic)
                .setMsgId(msgId)
                .setIsSuccess(isSuccess).build();
        channel.writeAndFlush(ack);
    }

    public void Disconnect() {
        ProducerMessage.Disconnect disconnect = ProducerMessage.Disconnect
            .newBuilder().build();
        channel.writeAndFlush(disconnect);
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
            ch.pipeline().addLast(handler);
        }
    }

}

