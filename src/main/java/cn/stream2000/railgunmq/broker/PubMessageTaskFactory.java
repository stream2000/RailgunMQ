package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;

public class PubMessageTaskFactory {

    private final static PubMessageTaskFactory factory = new PubMessageTaskFactory();
    private OfflineMessageStore offlineMessageStore;
    private PersistenceMessageStore persistenceMessageStore;
    private MessageDispatcher messageDispatcher;

    public static PubMessageTask newPubMessageTask(PubMessageRequest request) {
        return new PubMessageTask(request, factory.offlineMessageStore,
            factory.persistenceMessageStore,
            factory.messageDispatcher);
    }

    public static PubMessageTaskFactory getInstance() {
        return factory;
    }

    public void SetUpPubMessageTaskFactory(OfflineMessageStore offlineMessageStore,
        PersistenceMessageStore persistenceMessageStore, MessageDispatcher messageDispatcher) {
        this.offlineMessageStore = offlineMessageStore;
        this.persistenceMessageStore = persistenceMessageStore;
        this.messageDispatcher = messageDispatcher;
    }

}
