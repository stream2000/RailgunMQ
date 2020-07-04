package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.core.QueueMessage;
import cn.stream2000.railgunmq.core.StoredMessage;
import io.netty.channel.Channel;

// corresponding to a client
public class Subscription {
    private final String clientId;
    private final Channel channel;
    private final String topic;


    public Subscription(String clientId, Channel channel, String topic) {
        this.clientId = clientId;
        this.channel = channel;
        this.topic = topic;
    }

    // todo : send the message to consumer
    public void dispatchMessage(QueueMessage message){

    }

    public String getClientId() {
        return clientId;
    }

    public String getTopic() {
        return topic;
    }
}
