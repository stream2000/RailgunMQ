package cn.stream2000.railgunmq.consumer;

import cn.stream2000.railgunmq.core.ConsumerMessage;
import cn.stream2000.railgunmq.core.ConsumerMessage.SubMessage;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.core.SemaphoreCache;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RailgunMQConsumer {

    private final Channel channel;
    public String channelId;
    private BlockingQueue<ConsumerMessage.SubMessageAck> errorQueue;
    private BlockingQueue<SubMessage> messageQueue = new LinkedBlockingQueue<>();


    public RailgunMQConsumer(String host, int port, String topic, String connectionName)
        throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap =
            new Bootstrap().group(group).channel(NioSocketChannel.class)
                .handler(new ConsumerInitializer());
        errorQueue = new LinkedBlockingDeque<>();
        channel = bootstrap.connect(host, port).sync().channel();
        SemaphoreCache.acquire("producer init");
        ConsumerMessage.SubMessageRequest request = ConsumerMessage.SubMessageRequest
            .newBuilder()
            .setTopic(topic)
            .setName(connectionName)
            .build();
        channel.writeAndFlush(request);
    }

    //获取单个ACK
    public ConsumerMessage.SubMessageAck getAck() throws InterruptedException {
        return errorQueue.take();
    }


    //在限定时间内取一些ACK
    public List<ConsumerMessage.SubMessageAck> getAcks(long maxTime) {
        List<ConsumerMessage.SubMessageAck> acks = new ArrayList<>();
        long StartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - StartTime <= maxTime) {
            ConsumerMessage.SubMessageAck ack = errorQueue.poll();
            if (ack != null) {
                acks.add(ack);
            }
        }
        return acks;
    }

    public ConsumerMessage.SubMessage getMessage() throws InterruptedException {
        return messageQueue.take();
    }


    //在限定时间内取一些ACK
    public List<SubMessage> getMessages(long maxTime) throws InterruptedException {
        List<SubMessage> messages = new ArrayList<>();
        long StartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - StartTime <= maxTime) {
            SubMessage message = messageQueue.poll(100, TimeUnit.MILLISECONDS);
            if (message != null) {
                messages.add(message);
            }
        }
        return messages;
    }

    public int getAckNum() {
        return this.errorQueue.size();
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
            errorQueue.add(ack);
        }
    }

    public class SubMessageStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            ConsumerMessage.SubMessage msg = (SubMessage) message;
            messageQueue.add(msg);
        }
    }

    public class ClientInitStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            channelId = ((ProducerMessage.CreateChannelResponse) message).getChannelId();
            System.out.println("Response返回的channelid为：" + channelId);
            SemaphoreCache.release("producer init");
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
            router.registerHandler(SubMessage.getDefaultInstance(),
                new SubMessageStrategy());
            router.registerHandler(ProducerMessage.CreateChannelResponse.getDefaultInstance(),
                new ClientInitStrategy());
            ch.pipeline().addLast(handler);
        }
    }

}

