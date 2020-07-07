package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.QueueMessage;
import cn.stream2000.railgunmq.core.StoredMessage;
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
    private final Map<String, Boolean> ackMap = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(LoggerName.BROKER);
    private MessageDispatcher messageDispatcher;

    public AckManager(OfflineMessageStore offlineMessageStore,
        PersistenceMessageStore persistenceMessageStore
    ) {
        this.offlineMessageStore = offlineMessageStore;
        this.persistenceMessageStore = persistenceMessageStore;
    }

    public void setMessageDispatcher(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    public void monitorMessageAck(String topic, String msgId) {
        ackMap.compute(key(topic, msgId), (String key, Boolean oldValue) -> {
            if (oldValue == null || !oldValue) {
                hashedWheelTimer.newTimeout(this.new TimerFiredEvent(topic, msgId)::OnTimerFired, 5,
                    TimeUnit.SECONDS);
            }
            return true;
        });
    }

    public void ackMessage(String topic, String msgId) {
        ackMap.compute(key(topic,msgId), (key, oldValue) -> {
            if (oldValue != null && oldValue) {
                // TODO use a thread pool to execute time-consuming task
                log.debug("[AckManager] delete offline message with topic {} id {}", topic, msgId);
                offlineMessageStore.deleteMessage(topic,msgId);
            }
            return null;
        });
    }

    private String key(String topic, String msgId) {
        return topic + "-" + msgId;
    }

    private Pair<String, String> split(String key) {
        String[] result = StringUtils.split(key, "-");
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
            log.debug("[Timer] timer event fired with topic: {} id: {}",topic,msgId);
            ackMap.compute(key(topic, msgId), (k, ov) -> {
                if (ov == null || !ov) {
                    log.info("[Timer] message with topic: {} id: {} is already acknowledged",topic,msgId);
                    return null;
                } else {
                    // oldValue is true, so we haven't received the ack
                    StoredMessage msg = persistenceMessageStore.getMessage(topic, msgId);
                    return sendMessage(msg);
                }
            });
        }

        void retrySendingMessageWithBackoff(long delay, int times, StoredMessage msg) {
            if (!messageDispatcher.appendMessage(QueueMessage.storedMessageMapper(msg))) {
                // the dispatcher is still full, retry with a backoff
                final long newDelay = times > 5 ? 5000 : delay + 1000;
                hashedWheelTimer.newTimeout((t) ->
                        retrySendingMessageWithBackoff(newDelay, times + 1, msg), newDelay,
                    TimeUnit.MILLISECONDS);
            }
        }

        Boolean sendMessage(StoredMessage msg) {
            if (!messageDispatcher.appendMessage(QueueMessage.storedMessageMapper(msg))) {
                // the message queue is full, we will retry with a backoff
                hashedWheelTimer
                    .newTimeout((t) -> retrySendingMessageWithBackoff(1000, 1, msg), 1000,
                        TimeUnit.MILLISECONDS);
                return null;
            } else {
                return true;
            }
        }
    }
}
