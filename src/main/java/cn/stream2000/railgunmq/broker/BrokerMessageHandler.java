package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.core.ChannelMap;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.netty.MessageEventWrapper;
import cn.stream2000.railgunmq.netty.codec.MessageStrategyContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class BrokerMessageHandler extends MessageEventWrapper<Object> {

  public BrokerMessageHandler() {
    super.setWrapper(this);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ChannelMap.addChannel(ctx.channel().id().asLongText(), ctx.channel());
    ProducerMessage.CreateChannelResponse response =
        ProducerMessage.CreateChannelResponse.newBuilder().setChannelId(ctx.channel().id().asLongText()).build();
    ctx.writeAndFlush(response);
  }

  @Override
  public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message)
      throws Exception {
    MessageStrategyContext strategy = (MessageStrategyContext) message;
    strategy.invoke();
  }
}
