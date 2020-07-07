package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.broker.AckManager;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.ConsumerMessage;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: 19028
 * @date: 2020/7/7
 */
public class AckSubTask implements Callable<Void> {

    private final ConsumerMessage.SendMessageAck ack;
    private final AckManager ackManager;
    private final Logger log = LoggerFactory.getLogger(LoggerName.BROKER);

    public AckSubTask(ConsumerMessage.SendMessageAck ack, AckManager ackManager) {
        this.ack = ack;
        this.ackManager = ackManager;
    }

    @Override
    public Void call() throws Exception {
        ackManager.ackMessage(ack.getTopic(), ack.getMsgId());
        log.info("[FakeSubscription] ack message with topic: {} id: {}", ack.getTopic(),
            ack.getMsgId());
        return null;
    }
}
