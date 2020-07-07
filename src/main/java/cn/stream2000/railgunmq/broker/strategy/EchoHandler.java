package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.netty.MessageStrategy;
import io.netty.channel.ChannelHandlerContext;

public class EchoHandler implements MessageStrategy {

    @Override
    public void handleMessage(ChannelHandlerContext ctx, Object message) {
        ProducerMessage.PubMessageRequest request = (ProducerMessage.PubMessageRequest) message;
        System.out.println("receive letter with id " + request.getLetterId());
        ProducerMessage.PubMessageAck ack =
                ProducerMessage.PubMessageAck.newBuilder().setLetterId(request.getLetterId()).build();
        ctx.channel().writeAndFlush(ack);
    }

}
