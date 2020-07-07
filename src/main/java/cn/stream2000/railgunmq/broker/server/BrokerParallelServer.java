package cn.stream2000.railgunmq.broker.server;

import cn.stream2000.railgunmq.broker.AckManager;
import cn.stream2000.railgunmq.broker.MessageDispatcher;
import cn.stream2000.railgunmq.broker.PubMessageTaskFactory;
import cn.stream2000.railgunmq.broker.SendAckController;
import cn.stream2000.railgunmq.broker.subscribe.AckSubTaskFactory;
import cn.stream2000.railgunmq.broker.subscribe.OfflineFakeClientFactory;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.common.config.ServerConfig;
import cn.stream2000.railgunmq.common.config.StoreConfig;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;
import cn.stream2000.railgunmq.store.TopicStore;
import cn.stream2000.railgunmq.store.db.RDB;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrokerParallelServer {

    protected static int parallel = ServerConfig.getConfig().getParallel();
    protected static final ExecutorService businessPool = Executors.newFixedThreadPool(parallel);
    private MessageDispatcher messageDispatcher;

    public void init() {
        // init the core services
        StoreConfig storeConfig = new StoreConfig();
        RDB db = new RDB(storeConfig);
        db.init();

        OfflineMessageStore offlineMessageStore = new OfflineMessageStore(db);
        PersistenceMessageStore persistenceMessageStore = new PersistenceMessageStore(db);
        TopicStore topicStore = new TopicStore(db);

        TopicManager.setup(topicStore);
        int pollNum = Runtime.getRuntime().availableProcessors() * 2;

        AckManager ackManager = new AckManager(offlineMessageStore, persistenceMessageStore);
        MessageDispatcher messageDispatcher = new MessageDispatcher(pollNum, offlineMessageStore,
            persistenceMessageStore, ackManager);
        ackManager.setMessageDispatcher(messageDispatcher);
        PubMessageTaskFactory.getInstance()
            .SetUpPubMessageTaskFactory(offlineMessageStore, persistenceMessageStore,
                messageDispatcher, ackManager);
        this.messageDispatcher = messageDispatcher;
        OfflineFakeClientFactory
            .SetupOfflineFakeClientFactory(persistenceMessageStore, offlineMessageStore,
                messageDispatcher, ackManager);
        messageDispatcher.start();

        AckSubTaskFactory.getInstance().setupAckSubTaskFactory(offlineMessageStore, persistenceMessageStore, ackManager);
    }

    public void start() {
        messageDispatcher.start();
        for (int i = 0; i < parallel; i++) {
            businessPool.submit(new SendAckController());
        }
    }

    public void shutdown() {
        businessPool.shutdown();
    }
}
