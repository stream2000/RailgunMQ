package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.broker.subscribe.Subscription;
import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.common.helper.ThreadFactoryImpl;
import cn.stream2000.railgunmq.core.*;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDispatcher {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.BROKER);
    private final BlockingQueue<QueueMessage> freshQueue = new LinkedBlockingQueue<>();
    private final int pollThreadNum;
    private final OfflineMessageStore offlineMessageStore;
    private final PersistenceMessageStore persistenceMessageStore;
    private final AckManager ackManager;
    private volatile boolean stopped = false;
    private ThreadPoolExecutor pollThread;

    public MessageDispatcher(int pollThreadNum,
        OfflineMessageStore offlineMessageStore,
        PersistenceMessageStore persistenceMessageStore,
        AckManager ackManager) {
        this.pollThreadNum = pollThreadNum;
        this.offlineMessageStore = offlineMessageStore;
        this.persistenceMessageStore = persistenceMessageStore;
        this.ackManager = ackManager;
    }

    public void start() {
        this.pollThread = new ThreadPoolExecutor(pollThreadNum, pollThreadNum, 60 * 1000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(100000), new ThreadFactoryImpl("sendMessage2Subscriber"));

        new Thread(() -> {
            int waitTime = 100;
            while (!stopped) {
                try {
                    List<QueueMessage> messageList = new ArrayList<>(32);
                    // batch get
                    for (int i = 0; i < 32; i++) {
                        QueueMessage message = freshQueue.poll(waitTime, TimeUnit.MILLISECONDS);
                        if (Objects.nonNull(message)) {
                            log.debug("[MessageDispatcher]get message with topic: {} and id: {}",
                                message.getTopic(), message.getMsgId());
                            messageList.add(message);
                            waitTime = 100;
                        } else {
                            waitTime = 3000;
                            break;
                        }
                    }
                    if (messageList.size() > 0) {
                        AsyncDispatcher dispatcher = new AsyncDispatcher(messageList);
                        pollThread.submit(dispatcher);
                    }
                } catch (InterruptedException e) {
                    log.warn("[MessageDispatcher] poll message wrong.");
                }
            }
        }).start();
    }

    public boolean appendMessage(QueueMessage message) {
        boolean success = freshQueue.offer(message);
        if (!success) {
            log.warn("[PubMessage] -> the buffer queue is full");
        }
        return success;
    }

    public void shutdown() {
        this.stopped = true;
        this.pollThread.shutdown();
    }

    public class AsyncDispatcher implements Runnable {

        private final List<QueueMessage> messages;

        AsyncDispatcher(List<QueueMessage> messages) {
            this.messages = messages;
        }

        private void processPubMessageRequest(Topic topic, QueueMessage message) throws Exception {
            if (topic == null) {
                log.warn(
                    "[MessageDispatcher] topic {} not found, discard the message with id {}",
                    message.getTopic(), message.getMsgId());
                if (message.isNeedAck()) {
                    ProducerMessage.PubMessageAck ack = ProducerMessage.PubMessageAck
                        .newBuilder()
                        .setError(Message.ErrorType.InvalidTopic)
                        .setErrorMessage("invalid topic")
                        .setLetterId(message.getLetterId())
                        .setChannelId(message.getChannelId()).build();
                    ProducerAckQueue.pushAck(ack);
                }
                return;
            }

            // is a pubMessageRequest, we store it at first
            StoredMessage storedMessage = new StoredMessage(
                message.getTopic(), message.getMsgId(), message.getType(),
                message.getPayload());
            offlineMessageStore.addMessage(message.getTopic(), message.getMsgId());
            persistenceMessageStore.storeMessage(storedMessage);
            Subscription sub = topic.getNextSubscription();
            if (sub == null) {

                if (message.isNeedAck()) {
                    ProducerMessage.PubMessageAck ack = ProducerMessage.PubMessageAck
                            .newBuilder()
                            .setError(Message.ErrorType.OK)
                            .setErrorMessage("消息已被接收,但没有消费者")
                            .setLetterId(message.getLetterId())
                            .setChannelId(message.getChannelId())
                            .build();
                    ProducerAckQueue.pushAck(ack);
                }
                return;
            }
            // return ack to user
            if (message.isNeedAck()) {
                ProducerMessage.PubMessageAck ack = ProducerMessage.PubMessageAck
                    .newBuilder()
                        .setError(Message.ErrorType.OK)
                        .setErrorMessage("消息已被接收")
                    .setLetterId(message.getLetterId())
                    .setChannelId(message.getChannelId())
                    .build();

                ProducerAckQueue.pushAck(ack);
            }
            sub.dispatchMessage(message);
            ackManager.monitorMessageAck(message.getTopic(), message.getMsgId());
        }

        private void processStoredMessage(Topic topic, QueueMessage message) throws Exception {
            if (topic == null) {
                log.warn(
                    "[MessageDispatcher] topic {} not found, discard the message with id {}",
                    message.getTopic(), message.getMsgId());
                persistenceMessageStore
                    .releaseMessage(message.getTopic(), message.getMsgId());
                return;
            }
            Subscription sub = topic.getNextSubscription();
            if (sub == null) {
                log.info("[MessageDispatcher]no subscription");
                return;
            }
            // return ack to user
            sub.dispatchMessage(message);
            ackManager.monitorMessageAck(message.getTopic(), message.getMsgId());
        }


        @Override
        public void run() {
            if (Objects.nonNull(messages)) {
                try {
                    for (QueueMessage message : messages) {
                        Topic topic = TopicManager.getTopic(message.getTopic());
                        if (!StringUtils.isEmpty(message.getChannelId())) {
                            processPubMessageRequest(topic, message);
                        } else {
                            processStoredMessage(topic, message);
                        }
                    }
                } catch (Exception e) {
                    log.error("Dispatcher message failure,cause={}", e.getCause().toString());
                }
            }
        }

    }
}
