package cn.stream2000.railgunmq.producer;

import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.core.SemaphoreCache;
import cn.stream2000.railgunmq.netty.MessageStrategy;
import cn.stream2000.railgunmq.netty.codec.MessageStrategyProtobufDecoder;
import cn.stream2000.railgunmq.netty.codec.ProtoRouter;
import cn.stream2000.railgunmq.netty.codec.ProtobufEncoder;
import cn.stream2000.railgunmq.netty.codec.RouterInitializer;
import com.google.protobuf.ByteString;
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
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;


public class RailgunMQConnection {

    private final String host;
    private final int port;
    private Channel channel;
    private BlockingQueue<ProducerMessage.PubMessageAck> blockingQueue;
    private String channelId;
    private AtomicInteger atomicInteger;//自增id生成器

    //在初始化时指明IP和端口
    public RailgunMQConnection(String host, int port) throws InterruptedException {
        this.host = host;
        this.port = port;
        channelId = "";
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap =
            new Bootstrap().group(group).channel(NioSocketChannel.class)
                .handler(new ClientInitializer());
        channel = bootstrap.connect(host, port).sync().channel();
        SemaphoreCache.acquire("client init");
        blockingQueue = new LinkedBlockingDeque<ProducerMessage.PubMessageAck>();
        atomicInteger = new AtomicInteger();
    }

    public RailgunMQConnection(String host, int port, String connectionName)
        throws InterruptedException {
        this.host = host;
        this.port = port;
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap =
            new Bootstrap().group(group).channel(NioSocketChannel.class)
                .handler(new ClientInitializer());
        channel = bootstrap.connect(host, port).sync().channel();
        SemaphoreCache.acquire("client init");
        blockingQueue = new LinkedBlockingDeque<ProducerMessage.PubMessageAck>();
        atomicInteger = new AtomicInteger();
        SetChannelName(connectionName);
    }

    public void SetChannelName(String ChannelName) {
        int uuid = UUID.randomUUID().hashCode();
        ProducerMessage.SetChannelName setChannelName =
            ProducerMessage.SetChannelName.newBuilder().setChannelId(channelId)
                .setNewname(ChannelName).setLetterId(uuid).build();
        channel.writeAndFlush(setChannelName);
    }

    public String getChannelId() {
        return this.channelId;
    }

    public void Disconnect() {
        //发送关闭channel的消息
        try {
            int uuid = UUID.randomUUID().hashCode();
            ProducerMessage.Disconnect disconnect = ProducerMessage.Disconnect
                .newBuilder().setChannelId(channelId)
                .setLetterId(uuid).build();
            channel.writeAndFlush(disconnect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取单个ACK
    public ProducerMessage.PubMessageAck getAck() throws InterruptedException {
        return this.blockingQueue.poll();
    }

    //在限定时间内取一些ACK
    public List<ProducerMessage.PubMessageAck> getAcks(long Maxtime) {
        List<ProducerMessage.PubMessageAck> acks = new ArrayList<ProducerMessage.PubMessageAck>();
        long StartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - StartTime <= Maxtime) {
            ProducerMessage.PubMessageAck ack = blockingQueue.poll();
            if (ack != null) {
                acks.add(ack);
            }

        }
        return acks;
    }


    public int getAckNum() {
        return this.blockingQueue.size();
    }


    //这里的send不应该返回空值，先看吧
    //暂时不考虑消息发送策略
    public int Publish(String topic, String message) {
        try {
            int id = atomicInteger.getAndIncrement();
            ProducerMessage.PubMessageRequest request =
                ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
                    .setLetterId(id)
                    .setType(ProducerMessage.PubMessageRequest.payload_type.Text)
                    .setData(ByteString.copyFromUtf8(message))
                    .setTopic(topic).build();
            channel.writeAndFlush(request);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public int Publish(String topic, byte[] bytes) {
        try {
            int id = atomicInteger.getAndIncrement();
            ProducerMessage.PubMessageRequest request =
                ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
                    .setLetterId(id)
                    .setType(ProducerMessage.PubMessageRequest.payload_type.Binary)
                    .setData(ByteString.copyFrom(bytes))
                    .setTopic(topic).build();
            channel.writeAndFlush(request);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int Publish(String topic, int value) {
        try {
            int id = atomicInteger.getAndIncrement();
            ProducerMessage.PubMessageRequest request =
                ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
                    .setLetterId(id)
                    .setType(ProducerMessage.PubMessageRequest.payload_type.Integer)
                    .setData(ByteString.copyFromUtf8(Integer.toString(value)))
                    .setTopic(topic).build();
            channel.writeAndFlush(request);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //处理返回的函数，在ClientInitializer中注册
    public class PubMessageResponseStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            ProducerMessage.PubMessageAck ack = (ProducerMessage.PubMessageAck) message;
            //如果成功收到ack就关闭连接
            blockingQueue.add(ack);
        }
    }

    public class ClientInitStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            channelId = ((ProducerMessage.CreateChannelResponse) message).getChannelId();
            System.out.println("Response返回的channelid为：" + channelId);
            SemaphoreCache.release("client init");
        }
    }

    public class ClientInitializer extends ChannelInitializer<SocketChannel> {

        ProtobufEncoder encoder = new ProtobufEncoder(RouterInitializer.initialize());
        ProtoRouter router = RouterInitializer.initialize();
        ClientHandler handler = new ClientHandler();

        @Override
        protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(encoder);
            ch.pipeline().addLast(new MessageStrategyProtobufDecoder(router));
            router.registerHandler(ProducerMessage.PubMessageAck.getDefaultInstance(),
                new PubMessageResponseStrategy());
            router.registerHandler(ProducerMessage.CreateChannelResponse.getDefaultInstance(),
                new ClientInitStrategy());
            ch.pipeline().addLast(handler);
        }
    }
}
