package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.broker.AckManager;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.QueueMessage;
import io.netty.channel.Channel;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeSubscription extends Subscription {

    private final AckManager ackManager;
    private final Logger log = LoggerFactory.getLogger(LoggerName.BROKER);

    public FakeSubscription(String clientId, Channel channel, String topic, AckManager ackManager) {
        super(clientId, channel, topic, ackManager);
        this.ackManager = ackManager;
    }

    @Override
    public void dispatchMessage(QueueMessage message) {
        log.debug("[FakeSubscription] dispatch message with topic: {} id: {}", message.getTopic(),
            message.getMsgId());
        new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                log.debug("[FakeSubscription] ack message with topic: {} id: {}", message.getTopic(),
                    message.getMsgId());
                ackManager.ackMessage(message.getTopic(), message.getMsgId());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }
}
