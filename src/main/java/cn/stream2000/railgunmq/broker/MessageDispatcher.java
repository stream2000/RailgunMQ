package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.common.helper.ThreadFactoryImpl;
import cn.stream2000.railgunmq.core.InnerMessage;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDispatcher {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.BROKER);
    private final BlockingQueue<InnerMessage> freshQueue = new LinkedBlockingQueue<>();
    private final int pollThreadNum;
    private final OfflineMessageStore offlineMessageStore;
    private volatile boolean stopped = false;
    private ThreadPoolExecutor pollThread;

    public MessageDispatcher(int pollThreadNum,
        OfflineMessageStore offlineMessageStore) {
        this.pollThreadNum = pollThreadNum;
        this.offlineMessageStore = offlineMessageStore;
    }

    public void start() {
        this.pollThread = new ThreadPoolExecutor(pollThreadNum, pollThreadNum, 60 * 1000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(100000), new ThreadFactoryImpl("sendMessage2Subscriber"));

        new Thread(() -> {
            int waitTime = 100;
            while (!stopped) {
                try {
                    List<InnerMessage> messageList = new ArrayList(32);
                    // batch get
                    for (int i = 0; i < 32; i++) {
                        InnerMessage message = freshQueue.poll(waitTime, TimeUnit.MILLISECONDS);
                        if (Objects.nonNull(message)) {
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

    public boolean appendMessage(InnerMessage message) {
        boolean isNotFull = freshQueue.offer(message);
        if (!isNotFull) {
            log.warn("[PubMessage] -> the buffer queue is full");
        }
        return isNotFull;
    }

    public void shutdown() {
        this.stopped = true;
        this.pollThread.shutdown();
    }

    public class AsyncDispatcher implements Runnable {

        private final List<InnerMessage> messages;

        AsyncDispatcher(List<InnerMessage> messages) {
            this.messages = messages;
        }

        @Override
        public void run() {
            if (Objects.nonNull(messages)) {
                try {
                    for (InnerMessage message : messages) {
                        var topic = TopicManager.getTopic(message.getTopic());
                        if (topic != null) {
                            var sub = topic.getNextSubscription();
                            // store this message into offline messages
                            if (sub == null) {
                                offlineMessageStore
                                    .addMessage(message.getTopic(), message.getMsgId());
                            } else {
                                sub.dispatchMessage(message);
                            }
                        } else {
                            // FixMe: do something else instead of simply discarding the message
                            log.warn(
                                "[MessageDispatcher] topic {} not found, discard the message with id {}",
                                message.getTopic(), message.getMsgId());
                        }
                    }
                } catch (Exception e) {
                    log.warn("Dispatcher message failure,cause={}", e.getCause().toString());
                }
            }
        }
    }
}
