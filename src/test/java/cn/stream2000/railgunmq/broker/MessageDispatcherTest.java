package cn.stream2000.railgunmq.broker;


import cn.stream2000.railgunmq.broker.server.BrokerParallelServer;
import cn.stream2000.railgunmq.broker.subscribe.FakeSubscription;
import cn.stream2000.railgunmq.broker.subscribe.OfflineFakeClientFactory;
import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.common.config.StoreConfig;
import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest;
import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest.payload_type;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;
import cn.stream2000.railgunmq.store.TopicStore;
import cn.stream2000.railgunmq.store.db.RDB;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDispatcherTest {

    private static Logger log = LoggerFactory.getLogger("test");
    private BrokerParallelServer brokerParallelServer;
    private RDB db;
    private TopicStore topicStore;
    private OfflineMessageStore offlineMessageStore;
    private PersistenceMessageStore persistenceMessageStore;
    private MessageDispatcher messageDispatcher;
    private AckManager ackManager;

    @BeforeEach
    public void initComponents() {
        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setRocksDbPath("/tmp/rocksdb");
        db = new RDB(storeConfig);
        db.init();

        offlineMessageStore = new OfflineMessageStore(db);
        persistenceMessageStore = new PersistenceMessageStore(db);
        TopicStore topicStore = new TopicStore(db);

        TopicManager.setup(topicStore);
        int pollNum = Runtime.getRuntime().availableProcessors() * 2;

        ackManager = new AckManager(offlineMessageStore, persistenceMessageStore);
        messageDispatcher = new MessageDispatcher(pollNum, offlineMessageStore,
            persistenceMessageStore, ackManager);
        ackManager.setMessageDispatcher(messageDispatcher);
        PubMessageTaskFactory.getInstance()
            .SetUpPubMessageTaskFactory(offlineMessageStore, persistenceMessageStore,
                messageDispatcher, ackManager);
        OfflineFakeClientFactory
            .SetupOfflineFakeClientFactory(persistenceMessageStore, offlineMessageStore,
                messageDispatcher, ackManager);
        messageDispatcher.start();
    }

    private List<String> sendNMessage(int n) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            PubMessageRequest req = PubMessageRequest.newBuilder()
                .setChannelId("sdsdsdsdsd")
                .setTopic("default")
                .setType(payload_type.Integer)
                .setData(ByteString.copyFromUtf8(Integer.toString(i))).build();
            FutureTask<String> futureTask = new FutureTask<>(
                PubMessageTaskFactory.newPubMessageTask(req));
            BusinessTaskExecutor.getBusinessPool()
                .submit(futureTask);
            try {
                String id = futureTask.get();
                ids.add(id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return ids;
    }

    @Test
    public void testPersistence() {
        List<String> ids = sendNMessage(30);
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String id : ids) {
            Assert.assertTrue(offlineMessageStore.checkMessage("default", id));
            Assert.assertNotNull(persistenceMessageStore.getMessage("default", id));
        }
    }

    @Test
    public void testOfflineThenOnline() {
        List<String> ids = sendNMessage(30);
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String id : ids) {
            Assert.assertTrue(offlineMessageStore.checkMessage("default", id));
        }

        FakeSubscription fakeSubscription = new FakeSubscription("111", null, "default",
            ackManager);
        Topic defaultTopic = TopicManager.getTopic("default");
        defaultTopic.addSubscription(fakeSubscription);
        try {
            TimeUnit.MILLISECONDS.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAllOnline() {
        FakeSubscription fakeSubscription =
            new FakeSubscription("111", null, "default", ackManager);
        Topic defaultTopic = TopicManager.getTopic("default");

        defaultTopic.addSubscription(fakeSubscription);
        List<String> ids = sendNMessage(30);

        // you should reach agreement in maxWaitTime
        long maxWaitTime = 20000;
        Date date = new Date();
        long start = date.getTime();
        for (int i = 0; i < ids.size();) {
            String id = ids.get(i);
            if (offlineMessageStore.checkMessage("default", id)
                || persistenceMessageStore.getMessage("default", id) != null) {
                long current = new Date().getTime();
                if (current - start < maxWaitTime) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Assert.fail();
                    }
                }else {
                    Assert.fail();
                }
            }
            i++;
        }

        log.info("check all");
        try {
            TimeUnit.MILLISECONDS.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}