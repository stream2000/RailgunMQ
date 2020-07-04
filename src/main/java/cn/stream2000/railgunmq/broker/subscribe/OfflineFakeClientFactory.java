package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.broker.AckManager;
import cn.stream2000.railgunmq.broker.MessageDispatcher;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;

public class OfflineFakeClientFactory {

    private final static OfflineFakeClientFactory factory = new OfflineFakeClientFactory();
    private PersistenceMessageStore persistenceMessageStore;
    private OfflineMessageStore offlineMessageStore;
    private MessageDispatcher messageDispatcher;
    private AckManager ackManager;

    public static OfflineFakeClientFactory getInstance() {
        return factory;
    }

    public static void SetupOfflineFakeClientFactory(
        PersistenceMessageStore persistenceMessageStore, OfflineMessageStore offlineMessageStore,
        MessageDispatcher messageDispatcher, AckManager ackManager) {
        factory.persistenceMessageStore = persistenceMessageStore;
        factory.offlineMessageStore = offlineMessageStore;
        factory.messageDispatcher = messageDispatcher;
        factory.ackManager = ackManager;
    }

    public static OfflineFakeClient newOfflineFakeClient(String topic) {
        return new OfflineFakeClient(topic, factory.persistenceMessageStore,
            factory.offlineMessageStore, factory.messageDispatcher, factory.ackManager);
    }
}
