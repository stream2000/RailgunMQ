package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.broker.AckManager;
import cn.stream2000.railgunmq.core.ConsumerMessage;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;

public class AckSubTaskFactory {

    private final static AckSubTaskFactory factory = new AckSubTaskFactory();
    private OfflineMessageStore offlineMessageStore;
    private PersistenceMessageStore persistenceMessageStore;
    private AckManager ackManager;

    public static AckSubTask newAckSubTask(ConsumerMessage.SendMessageAck ack) {
        return new AckSubTask(ack, factory.ackManager);
    }

    public static AckSubTaskFactory getInstance() {
        return factory;
    }

    public void setupAckSubTaskFactory(OfflineMessageStore offlineMessageStore,
                                       PersistenceMessageStore persistenceMessageStore,
                                       AckManager ackManager) {
        this.offlineMessageStore = offlineMessageStore;
        this.persistenceMessageStore = persistenceMessageStore;
        this.ackManager = ackManager;
    }
}
