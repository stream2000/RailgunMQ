package cn.stream2000.railgunmq.broker;


import cn.stream2000.railgunmq.broker.server.BrokerParallelServer;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.common.config.StoreConfig;
import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest;
import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest.payload_type;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.TopicStore;
import cn.stream2000.railgunmq.store.db.RDB;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDispatcherTest {

    private BrokerParallelServer brokerParallelServer;
    private static Logger log = LoggerFactory.getLogger("test");

    private RDB rdb;
    private TopicStore topicStore;
    private OfflineMessageStore offlineMessageStore;

    @BeforeEach
    public void setUp() {
        StoreConfig storeConfig = new StoreConfig();
        rdb = new RDB(storeConfig);
        rdb.init();

        topicStore = new TopicStore(rdb);
        offlineMessageStore = new OfflineMessageStore(rdb);
        TopicManager.setup(topicStore);
        brokerParallelServer = new BrokerParallelServer();
        brokerParallelServer.initWithDB(rdb);
//        brokerParallelServer.start();
    }

    @Test
    public void mock() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
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
            } catch (Exception ignore) {

            }
        }
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String id : ids) {
            boolean ok = offlineMessageStore.checkMessage("default", id);
            Assert.assertTrue(ok);
        }
        offlineMessageStore.getMessages("default", "", 30);

    }
}