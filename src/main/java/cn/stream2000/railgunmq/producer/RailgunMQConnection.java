package cn.stream2000.railgunmq.producer;

import cn.stream2000.railgunmq.core.Connection;
import cn.stream2000.railgunmq.core.Connection.ConnectionRole;
import cn.stream2000.railgunmq.core.ProducerMessage;
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

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
@ChannelHandler.Sharable
public class RailgunMQConnection {
    private final String host;
    private final int port;
    Connection connection;
    public BlockingQueue<ProducerMessage.PubMessageAck> blockingQueue;
    public String channelId;
    //在初始化时指明IP和端口
    public RailgunMQConnection(String host, int port) throws InterruptedException {
        this.host = host;
        this.port = port;
        channelId="";
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap =
                new Bootstrap().group(group).channel(NioSocketChannel.class)
                        .handler(new ClientInitializer());
        Channel channel= bootstrap.connect(host,port).sync().channel();
        SemaphoreCache.acquire("client init");
        connection=new Connection("balabala", channel, ConnectionRole.Consumer);


        blockingQueue=new LinkedBlockingDeque<ProducerMessage.PubMessageAck>();
    }
    public RailgunMQConnection(String host, int port, String connectionName) throws InterruptedException {
        this.host = host;
        this.port = port;
        EventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap =
                new Bootstrap().group(group).channel(NioSocketChannel.class)
                        .handler(new ClientInitializer());
        Channel channel= bootstrap.connect(host,port).sync().channel();
        SemaphoreCache.acquire("client init");
        connection=new Connection(connectionName, channel, ConnectionRole.Consumer);
        blockingQueue=new LinkedBlockingDeque<ProducerMessage.PubMessageAck>();
        SetChannelName(connectionName);
    }

    public void SetChannelName(String ChannelName)
    {
        int uuid =UUID.randomUUID().hashCode();
        ProducerMessage.SetChannelName setChannelName=
                ProducerMessage.SetChannelName.newBuilder().setChannelId(channelId)
                        .setNewname(ChannelName).setLetterId(uuid).build();
        connection.getChannel().writeAndFlush(setChannelName);
    }


    public void Disconnect()
    {
        //发送关闭channel的消息
        try {
            int uuid =UUID.randomUUID().hashCode();
            ProducerMessage.Disconnect disconnect=ProducerMessage.Disconnect
                    .newBuilder().setChannelId(channelId)
                    .setLetterId(uuid).build();
            connection.getChannel().writeAndFlush(disconnect);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //这里的send不应该返回空值，先看吧
    //暂时不考虑消息发送策略
    public void Publish(String topic,String message)
    {
        try {
            int uuid = UUID.randomUUID().hashCode();
            ProducerMessage.PubMessageRequest request =
                    ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
                            .setLetterId(uuid)
                            .setType(ProducerMessage.PubMessageRequest.payload_type.Text)
                            .setData(ByteString.copyFromUtf8(message))
                            .setTopic(topic).build();
            connection.getChannel().writeAndFlush(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void Publish(String topic, byte[] bytes)
    {
        try {
            int uuid = UUID.randomUUID().hashCode();
            ProducerMessage.PubMessageRequest request =
                    ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
                            .setLetterId(uuid)
                            .setType(ProducerMessage.PubMessageRequest.payload_type.Binary)
                            .setData(ByteString.copyFrom(bytes))
                            .setTopic(topic).build();
            connection.getChannel().writeAndFlush(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Publish(String topic, int value)
    {
        try {
            int uuid = UUID.randomUUID().hashCode();
            ProducerMessage.PubMessageRequest request =
                    ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
                            .setLetterId(uuid)
                            .setType(ProducerMessage.PubMessageRequest.payload_type.Integer)
                            .setData(ByteString.copyFromUtf8(Integer.toString(value)))
                            .setTopic(topic).build();
           connection.getChannel().writeAndFlush(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //处理返回的函数，在ClientInitializer中注册
    public class PubMessageResponseStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            ProducerMessage.PubMessageAck ack= (ProducerMessage.PubMessageAck)message;
            //如果成功收到ack就关闭连接
            blockingQueue.add(ack);
        }
    }

    public class ClientInitStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            channelId = ((ProducerMessage.CreateChannelResponse) message).getChannelId();
            System.out.println("Response返回的channelid为："+channelId);
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
