package cn.stream2000.railgunmq.netty;

import io.netty.channel.ChannelHandlerContext;

// may have some field to set.
public interface MessageEventHandler {

  void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) throws Exception;
}
