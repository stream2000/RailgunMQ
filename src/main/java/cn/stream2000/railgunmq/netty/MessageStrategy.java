package cn.stream2000.railgunmq.netty;

import io.netty.channel.ChannelHandlerContext;

public interface MessageStrategy {

    void handleMessage(ChannelHandlerContext channelHandlerContext, Object message);
}
