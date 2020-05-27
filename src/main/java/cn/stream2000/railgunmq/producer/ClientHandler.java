package cn.stream2000.railgunmq.producer;

import cn.stream2000.railgunmq.netty.MessageEventWrapper;
import cn.stream2000.railgunmq.netty.codec.MessageStrategyContext;
import io.netty.channel.ChannelHandlerContext;

public class ClientHandler extends MessageEventWrapper<Object> {
    public ClientHandler() {
        super.setWrapper(this);
    }

    @Override public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        MessageStrategyContext strategy = (MessageStrategyContext)message;
        strategy.invoke();
    }
}
