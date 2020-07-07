package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;

public class PubMessageTaskFactory {

    private final static PubMessageTaskFactory factory = new PubMessageTaskFactory();
    private OfflineMessageStore offlineMessageStore;
    private PersistenceMessageStore persistenceMessageStore;
    private MessageDispatcher messageDispatcher;
    private AckManager ackManager;

    public static PubMessageTask newPubMessageTask(ProducerMessage.PubMessageRequest request) {
        return new PubMessageTask(request, factory.messageDispatcher);
    }

    public static PubMessageTaskFactory getInstance() {
        return factory;
    }

    public void SetUpPubMessageTaskFactory(OfflineMessageStore offlineMessageStore,
        PersistenceMessageStore persistenceMessageStore, MessageDispatcher messageDispatcher,
        AckManager ackManager) {
        this.offlineMessageStore = offlineMessageStore;
        this.persistenceMessageStore = persistenceMessageStore;
        this.messageDispatcher = messageDispatcher;
        this.ackManager = ackManager;
    }

}
