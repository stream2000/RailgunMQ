package cn.stream2000.railgunmq.netty.codec;

import cn.stream2000.railgunmq.broker.strategy.MessageStrategy;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

public class MessageStrategyContext {
    private final Object msg;
    private final MessageStrategy strategy;
    private final ChannelHandlerContext context;

    public MessageStrategyContext(Object msg, MessageStrategy strategy, ChannelHandlerContext context) {
        this.msg = msg;
        this.strategy = strategy;
        this.context = context;
    }

    public Object getMsg() {
        return msg;
    }

    public MessageStrategy getStrategy() {
        return strategy;
    }

    public void setBusinessPool(ExecutorService executorService) {
        strategy.setBusinessPool(executorService);
    }

    public void invoke() throws Exception {
        strategy.handleMessage(context, msg);
    }
}
