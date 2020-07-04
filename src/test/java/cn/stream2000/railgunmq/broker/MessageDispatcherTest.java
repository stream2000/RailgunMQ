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
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Pair;
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
    private void initComponents() {
        StoreConfig storeConfig = new StoreConfig();
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


    @Test
    public void onlineMock() {

        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
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
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String id : ids) {
            Assert.assertTrue(offlineMessageStore.checkMessage("default", id));
        }

        Pair<List<byte[]>, byte[]> pair = offlineMessageStore.getMessages("default", "", 30);

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


}