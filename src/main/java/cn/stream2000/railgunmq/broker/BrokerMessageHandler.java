package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.netty.codec.MessageStrategyContext;
import cn.stream2000.railgunmq.core.ChannelMap;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.netty.MessageEventWrapper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

@ChannelHandler.Sharable public class BrokerMessageHandler extends MessageEventWrapper<Object> {
    public BrokerMessageHandler() {
        super.setWrapper(this);
    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String uuid = UUID.randomUUID().toString();
        ChannelMap.addChannel(uuid, ctx.channel());
        ProducerMessage.CreateChannelResponse response =
            ProducerMessage.CreateChannelResponse.newBuilder().setChannelId(uuid).build();
        ctx.writeAndFlush(response);
    }

    @Override public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
        MessageStrategyContext strategy = (MessageStrategyContext)message;
        strategy.invoke();
    }
}