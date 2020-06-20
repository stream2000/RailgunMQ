package cn.stream2000.railgunmq.producer;

import cn.stream2000.railgunmq.core.Message;
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

public class RailgunMQClient {
    private final String host;
    private final int port;
    private static RailgunMQClient railgunMQClientInstance=new RailgunMQClient("127.0.0.1",9999);
    String channelId = "";
    public static RailgunMQClient getRailgunMQClientInstance()
    {
        return railgunMQClientInstance;
    }

    //在初始化时指明IP和端口
    private RailgunMQClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //这里的send不应该返回空值，先看吧
    //暂时不考虑消息发送策略
    public void Publish(String topic,String message)
    {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap =
                    new Bootstrap().group(group).channel(NioSocketChannel.class)
                            .handler(new ClientInitializer());
            Channel channel = bootstrap.connect(host, port).sync().channel();
            SemaphoreCache.acquire("client init");
            ProducerMessage.PubMessageRequest request =
                    ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
                            .setType(ProducerMessage.PubMessageRequest.payload_type.Text)
                            .setData(ByteString.copyFromUtf8(message))
                            .setTopic(topic).build();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }

    public void Publish(String topic, byte[] bytes)
    {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap =
                    new Bootstrap().group(group).channel(NioSocketChannel.class)
                            .handler(new ClientInitializer());
            Channel channel = bootstrap.connect(host, port).sync().channel();
            SemaphoreCache.acquire("client init");
            ProducerMessage.PubMessageRequest request =
                    ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
                            .setType(ProducerMessage.PubMessageRequest.payload_type.Binary)
                            .setData(ByteString.copyFrom(bytes))
                            .setTopic(topic).build();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void Publish(String topic, int value)
    {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap =
                    new Bootstrap().group(group).channel(NioSocketChannel.class)
                            .handler(new ClientInitializer());
            Channel channel = bootstrap.connect(host, port).sync().channel();
            SemaphoreCache.acquire("client init");
            ProducerMessage.PubMessageRequest request =
                    ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
                            .setType(ProducerMessage.PubMessageRequest.payload_type.Integer)
                            .setData(ByteString.copyFromUtf8(Integer.toString(value)))
                            .setTopic(topic).build();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
    //处理返回的函数，在ClientInitializer中注册
    public static class PubMessageResponseStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            ProducerMessage.PubMessageAck ack= (ProducerMessage.PubMessageAck)message;
            System.out.println("返回码为：  "+ack.getError());
            System.out.println(ack.getErrorMessage());
            //如果成功收到ack就关闭连接
            if(ack.getError()== Message.ErrorType.OK)
            {
                channelHandlerContext.close();
            }
            else
            {

            }
        }
    }

    public class ClientInitStrategy implements MessageStrategy {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            channelId = ((ProducerMessage.CreateChannelResponse) message).getChannelId();
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
