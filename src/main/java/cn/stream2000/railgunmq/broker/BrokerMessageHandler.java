package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.netty.MessageEventWrapper;
import cn.stream2000.railgunmq.netty.codec.MessageStrategyContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable public class BrokerMessageHandler extends MessageEventWrapper<Object> {
    public BrokerMessageHandler() {
        super.setWrapper(this);
    }

    @Override public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        MessageStrategyContext strategy = (MessageStrategyContext)message;
        strategy.setBusinessPool(businessPool);
        strategy.invoke();
    }
}
