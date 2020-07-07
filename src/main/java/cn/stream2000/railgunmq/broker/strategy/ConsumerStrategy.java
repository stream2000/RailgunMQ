package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.broker.subscribe.AckSubTaskFactory;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.ConsumerMessage;
import cn.stream2000.railgunmq.core.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerStrategy {

    private static Logger log = LoggerFactory.getLogger(LoggerName.BROKER);

    public static class SubscribeMessageStrategy implements MessageStrategyWithBusinessPool {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {

            ConsumerMessage.SubMessageRequest request = (ConsumerMessage.SubMessageRequest) message;
            String topic = request.getTopic();
            ConsumerMessage.SubMessageAck ack;
            if (TopicManager.getAll().contains(topic)) {
                ack = ConsumerMessage.SubMessageAck.newBuilder().setError(Message.ErrorType.OK).setErrorMessage("topic订阅成功").build();
            } else {
                ack = ConsumerMessage.SubMessageAck.newBuilder().setError(Message.ErrorType.InvalidTopic).setErrorMessage("topic订阅失败").build();
            }
            System.out.println("返回码为：  " + ack.getError());
            System.out.println(ack.getErrorMessage());
            channelHandlerContext.writeAndFlush(ack);

        }
    }

    public static class AckMessageStrategy implements MessageStrategyWithBusinessPool {

        @Override
        public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message) {
            ConsumerMessage.SendMessageAck ack = (ConsumerMessage.SendMessageAck) message;
            getBusinessPool().submit(AckSubTaskFactory.newAckSubTask(ack));
        }
    }
}
