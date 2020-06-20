package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.broker.AckManager;
import cn.stream2000.railgunmq.broker.MessageDispatcher;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;

public class OfflineFakeClientFactory {

    private final static OfflineFakeClientFactory factory = new OfflineFakeClientFactory();
    private PersistenceMessageStore persistenceMessageStore;
    private MessageDispatcher messageDispatcher;
    private AckManager ackManager;

    public static OfflineFakeClientFactory getInstance() {
        return factory;
    }

    public static void SetupOfflineFakeClientFactory(PersistenceMessageStore persistenceMessageStore,
        MessageDispatcher messageDispatcher,AckManager ackManager){
        factory.persistenceMessageStore = persistenceMessageStore;
        factory.messageDispatcher = messageDispatcher;
        factory.ackManager = ackManager;
    }

    public static OfflineFakeClient newOfflineFakeClient(String topic) {
        return new OfflineFakeClient(topic, factory.persistenceMessageStore,
            factory.messageDispatcher, factory.ackManager);
    }
}
