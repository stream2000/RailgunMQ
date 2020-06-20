package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.core.InnerMessage;
import cn.stream2000.railgunmq.core.ProducerAckQueue;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest;
import cn.stream2000.railgunmq.store.OfflineMessageStore;
import cn.stream2000.railgunmq.store.PersistenceMessageStore;
import java.util.UUID;
import java.util.concurrent.Callable;

public class PubMessageTask implements Callable<Void> {

    private final ProducerMessage.PubMessageRequest request;
    private final OfflineMessageStore offlineMessageStore;
    private final PersistenceMessageStore persistenceMessageStore;
    private final MessageDispatcher messageDispatcher;
    private final AckManager ackManager;

    public PubMessageTask(PubMessageRequest request,
        OfflineMessageStore offlineMessageStore,
        PersistenceMessageStore persistenceMessageStore,
        MessageDispatcher messageDispatcher, AckManager ackManager) {
        this.request = request;
        this.offlineMessageStore = offlineMessageStore;
        this.persistenceMessageStore = persistenceMessageStore;
        this.messageDispatcher = messageDispatcher;
        this.ackManager = ackManager;
    }

    @Override
    public Void call() throws Exception {
        Topic topic = TopicManager.getInstance().getTopic(request.getTopic());
        if (topic == null) {
            // the topic producer desired doesn't exist
            //  TODO return error message to the corresponding producer. assigned ZX
            return null;
        }

        String msgId = UUID.randomUUID().toString();
        InnerMessage msg = new InnerMessage(request.getTopic(), msgId,
            request.getType().getNumber(), request.getData().toByteArray());
        persistenceMessageStore.storeMessage(msg);
        var isOffline = false;

        synchronized (topic) {
            if (!topic.isActive()) {
                offlineMessageStore.addMessage(topic.getTopicName(), msgId);
                isOffline = true;
            }
        }

        if (!isOffline) {
            if (!messageDispatcher.appendMessage(msg)) {
                // the message queue is full
                // TODO return an error message to the producer to tell him that the message is discarded
                return null;
            } else {
                // start to monitor the ack of this message
                ackManager.monitorMessageAck(msg.getTopic(),msgId);
            }
        }

        // return ack to user
        ProducerMessage.PubMessageAck ack = ProducerMessage.PubMessageAck.newBuilder()
            .setLetterId(request.getLetterId())
            .setChannelId(request.getChannelId())
            .build();
        ProducerAckQueue.pushAck(ack);
        return null;
    }
}
