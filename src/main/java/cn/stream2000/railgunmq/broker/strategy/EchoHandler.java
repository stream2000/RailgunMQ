package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.core.ProducerMessage;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

public class EchoHandler implements MessageStrategy {
    ExecutorService businessPool;

    @Override public void handleMessage(ChannelHandlerContext ctx, Object message) {
        ProducerMessage.PubMessageRequest request = (ProducerMessage.PubMessageRequest)message;
        System.out.println("receive letter with id " + request.getLetterId());
        ProducerMessage.PubMessageAck ack =
            ProducerMessage.PubMessageAck.newBuilder().setLetterId(request.getLetterId()).build();
        ctx.channel().writeAndFlush(ack);
    }

    @Override public void setBusinessPool(ExecutorService executorService) {
        businessPool = executorService;
    }
}
