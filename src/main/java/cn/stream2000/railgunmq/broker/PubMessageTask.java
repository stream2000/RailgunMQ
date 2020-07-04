package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.core.Message;
import cn.stream2000.railgunmq.core.ProducerAckQueue;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest;
import cn.stream2000.railgunmq.core.QueueMessage;
import java.util.UUID;
import java.util.concurrent.Callable;

public class PubMessageTask implements Callable<Void> {

    private final ProducerMessage.PubMessageRequest request;
    private final MessageDispatcher messageDispatcher;

    public PubMessageTask(PubMessageRequest request, MessageDispatcher messageDispatcher) {
        this.request = request;
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public Void call() throws Exception {

        String msgId = UUID.randomUUID().toString();
        if (!messageDispatcher
            .appendMessage(QueueMessage.buildFromPubMessageRequest(request, msgId, true))) {
            // the message queue is full
            ProducerMessage.PubMessageAck ack = ProducerMessage.PubMessageAck.newBuilder()
                .setError(Message.ErrorType.FullMessageQuene)
                .setErrorMessage("the message is discarded")
                .setLetterId(request.getLetterId())
                .setChannelId(request.getChannelId()).build();
            ProducerAckQueue.pushAck(ack);
        }
        return null;
    }
}
