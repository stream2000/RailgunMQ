package cn.stream2000.railgunmq.broker.strategy;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

public interface MessageStrategy {
    void handleMessage(ChannelHandlerContext channelHandlerContext, Object message);

    default void setBusinessPool(ExecutorService executorService) {

    }
}
