package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.InnerMessage;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AckManager {

    private final HashedWheelTimer hashedWheelTimer = new HashedWheelTimer(1000,
        TimeUnit.MILLISECONDS, 16);
    private final OfflineMessageStore offlineMessageStore;
    private final PersistenceMessageStore persistenceMessageStore;
    private final MessageDispatcher messageDispatcher;
    private final Map<String, Boolean> ackMap = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(LoggerName.BROKER);

    public AckManager(OfflineMessageStore offlineMessageStore,
        PersistenceMessageStore persistenceMessageStore,
        MessageDispatcher messageDispatcher) {
        this.offlineMessageStore = offlineMessageStore;
        this.persistenceMessageStore = persistenceMessageStore;
        this.messageDispatcher = messageDispatcher;
    }

    public void monitorMessageAck(String topic, String msgId) {
        ackMap.compute(key(topic, msgId), (key, oldValue) -> {
            if (oldValue == null || !oldValue) {
                hashedWheelTimer.newTimeout(this.new TimerFiredEvent(topic, msgId)::OnTimerFired, 5,
                    TimeUnit.SECONDS);
            }
            return true;
        });
    }

    public void ackMessage(String topic, String msgId) {
        ackMap.compute(msgId, (key, oldValue) -> {
            if (oldValue != null && oldValue) {
                var p = split(key);
                // TODO use a thread pool to execute time-consuming task
                offlineMessageStore.deleteMessage(p.getLeft(), p.getRight());
            }
            return null;
        });
    }

    private String key(String topic, String msgId) {
        return topic + "-" + msgId;
    }

    private Pair<String, String> split(String key) {
        var result = StringUtils.split(key, "-");
        return Pair.of(result[0], result[1]);
    }

    class TimerFiredEvent {

        private final String topic;
        private final String msgId;

        TimerFiredEvent(String topic, String msgId) {
            this.topic = topic;
            this.msgId = msgId;
        }

        void OnTimerFired(Timeout timeout) {
            ackMap.compute(key(topic, msgId), (k, ov) -> {
                if (ov == null || !ov) {
                    return null;
                } else {
                    // oldValue is true, so we haven't received the ack
                    var t = TopicManager.getInstance().getTopic(topic);
                    if (t == null) {
                        // the topic is deleted, so we discard the message
                        offlineMessageStore.deleteMessage(topic, msgId);
                        persistenceMessageStore.releaseMessage(topic, msgId);
                        return null;
                    }
                    if (!t.isActive()) {
                        // the topic is inActive currently, so we return null directly
                        return null;
                    } else {
                        // there exists another subscriber so we push the message to the dispatcher
                        var msg = persistenceMessageStore.getMessage(topic, msgId);
                        return sendMessage(msg);
                    }
                }
            });
        }

        void retrySendingMessageWithBackoff(long delay, int times, InnerMessage msg) {
            if (!messageDispatcher.appendMessage(msg)) {
                // the dispatcher is still full, retry with a backoff
                final long newDelay = times > 5 ? 5000 : delay + 1000;
                hashedWheelTimer.newTimeout((t) ->
                        retrySendingMessageWithBackoff(newDelay, times + 1, msg), newDelay,
                    TimeUnit.MILLISECONDS);
            } else {
                // successfully sent the message, start to monitor the ack of this message
                monitorMessageAck(msg.getTopic(), msgId);
            }
        }

        Boolean sendMessage(InnerMessage msg) {
            if (!messageDispatcher.appendMessage(msg)) {
                // the message queue is full, we will retry with a backoff
                hashedWheelTimer.newTimeout((t) -> retrySendingMessageWithBackoff(1000, 1, msg), 1000,
                    TimeUnit.MILLISECONDS);
                return null;
            } else {
                // start to monitor the ack of this message
                hashedWheelTimer.newTimeout(new TimerFiredEvent(topic, msgId)::OnTimerFired, 5,
                    TimeUnit.SECONDS);
                return true;
            }
        }
    }
}
