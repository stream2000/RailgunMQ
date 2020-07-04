package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.broker.AckManager;
import cn.stream2000.railgunmq.broker.MessageDispatcher;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.QueueMessage;
import cn.stream2000.railgunmq.core.StoredMessage;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// when a topic becomes active, we start a fake
// client to send its offline messages
// if there exists no offline message, we wait for a second and try it again.
public class OfflineFakeClient implements Runnable {

    private final String topic;
    private final PersistenceMessageStore persistenceMessageStore;
    private final OfflineMessageStore offlineMessageStore;
    private final MessageDispatcher messageDispatcher;
    private final Logger log = LoggerFactory.getLogger(LoggerName.BROKER);
    private final AckManager ackManager;
    private volatile boolean stopped;

    public OfflineFakeClient(String topic,
        PersistenceMessageStore persistenceMessageStore,
        OfflineMessageStore offlineMessageStore,
        MessageDispatcher messageDispatcher, AckManager ackManager) {
        this.topic = topic;
        this.persistenceMessageStore = persistenceMessageStore;
        this.offlineMessageStore = offlineMessageStore;
        this.messageDispatcher = messageDispatcher;
        this.ackManager = ackManager;
    }

    public void stop() {
        stopped = true;
    }

    @Override
    public void run() {
        // poll messages from offlineMessageStore, then send them to dispatcher
        log.info("[OfflineFakeClient] start offline client of topic {} ", topic);
        byte[] startKey = new byte[0];
        long retryTime = 0;
        while (!stopped) {

            Pair<List<byte[]>, byte[]> pair;
            if (startKey.length == 0) {
                pair = offlineMessageStore.getMessages(topic, "", 30);
            } else {
                pair = offlineMessageStore.getMessages(startKey, 30);
            }

            if (pair.getLeft().size() == 0) {
                // no message available
                if (retryTime < 2) {
                    retryTime++;
                    try {
                        TimeUnit.MILLISECONDS.sleep(retryTime * 500);
                        continue;
                    } catch (Exception ex) {
                        return;
                    }
                } else {
                    // there exists no offline message
                    return;
                }
            }

            log.debug("[OfflineFakeClient] fetched {} messages", pair.getLeft().size());

            List<byte[]> ids = pair.getLeft();

            for (int i = 0; i < ids.size(); ) {
                String compound = new String(ids.get(i), StandardCharsets.UTF_8);
                String[] kv = compound.split("-", 3);
                if (kv.length != 3) {
                    i++;
                    continue;
                }
                String topic = kv[1];
                String id = kv[2];

                if (StringUtils.isEmpty(topic) || StringUtils.isEmpty(id)) {
                    i++;
                    continue;
                }

                StoredMessage msg = persistenceMessageStore.getMessage(topic, id);
                if (msg == null) {
                    i++;
                    continue;
                }

                if (!messageDispatcher.appendMessage(QueueMessage.storedMessageMapper(msg))) {
                    // a rude implementation: try until the end of the world
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                        continue;
                    } catch (InterruptedException ex) {
                        log.warn(
                            "[OfflineFakeClient]: be interrupted, discard all messages and return");
                        return;
                    }
                }
                i++;
            }

            startKey = pair.getRight();
            if (startKey.length == 0) {
                stopped = true;
            }
        }
    }
}
