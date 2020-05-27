package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.broker.PubMessageTask;
import cn.stream2000.railgunmq.core.ProducerMessage;
import io.netty.channel.ChannelHandlerContext;

public class ProducerStrategy {
    public static class PublishMessageStrategy implements MessageStrategyWithBusinessPool {

        @Override public void handleMessage(ChannelHandlerContext ctx, Object message) {
            ProducerMessage.PubMessageRequest request = (ProducerMessage.PubMessageRequest)message;
            getBusinessPool().submit(new PubMessageTask(request));
        }

    }
}
