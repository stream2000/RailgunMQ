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

public class NettyClient {

  private final String host;
  private final int port;
  String channelId = "";

  public NettyClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public static void main(String[] args) throws Exception {
    new NettyClient("127.0.0.1", 9999).run();
  }

  public void run() throws Exception {
    EventLoopGroup group = new NioEventLoopGroup(8);
    try {
      Bootstrap bootstrap =
          new Bootstrap().group(group).channel(NioSocketChannel.class)
              .handler(new ClientInitializer());
      Channel channel = bootstrap.connect(host, port).sync().channel();
      SemaphoreCache.acquire("client init");
      ProducerMessage.PubMessageRequest request =
          ProducerMessage.PubMessageRequest.newBuilder().setChannelId(channelId)
              .setData(ByteString.copyFromUtf8("hello world")).build();
      channel.writeAndFlush(request);
      channel.closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      group.shutdownGracefully();
    }
  }

  public static class PubMessageResponseStrategy implements MessageStrategy {

    @Override
    public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
      System.out.println(message);
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

