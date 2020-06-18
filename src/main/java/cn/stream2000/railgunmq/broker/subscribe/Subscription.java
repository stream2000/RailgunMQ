package cn.stream2000.railgunmq.broker.subscribe;

import java.nio.channels.Channel;

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

    public String getClientId() {
        return clientId;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getTopic() {
        return topic;
    }
}
