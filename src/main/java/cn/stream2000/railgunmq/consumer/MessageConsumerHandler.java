package cn.stream2000.railgunmq.consumer;

import cn.stream2000.railgunmq.netty.MessageEventWrapper;
import cn.stream2000.railgunmq.netty.codec.MessageStrategyContext;
import io.netty.channel.ChannelHandlerContext;

public class MessageConsumerHandler extends MessageEventWrapper<Object> {

    public MessageConsumerHandler() {
        super.setWrapper(this);
    }

    @Override
    public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message)
            throws Exception {
        MessageStrategyContext strategy = (MessageStrategyContext) message;
        strategy.invoke();
    }
}
