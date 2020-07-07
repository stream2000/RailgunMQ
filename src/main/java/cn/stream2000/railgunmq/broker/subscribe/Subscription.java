package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.broker.AckManager;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.QueueMessage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// corresponding to a client
public class Subscription {
    private final String clientId;
    private final Channel channel;
    private final String topic;
    private final Logger log = LoggerFactory.getLogger(LoggerName.BROKER);

    public Subscription(String clientId, Channel channel, String topic) {
        this.clientId = clientId;
        this.channel = channel;
        this.topic = topic;
    }

    public void dispatchMessage(QueueMessage message) {
        log.debug("[Subscription] dispatch message with topic: {} id: {}", message.getTopic(),
                message.getMsgId());
        //todo: 一对多
        channel.writeAndFlush(message);
    }

    public String getClientId() {
        return clientId;
    }

    public String getTopic() {
        return topic;
    }
}
