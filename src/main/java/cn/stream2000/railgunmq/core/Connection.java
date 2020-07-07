package cn.stream2000.railgunmq.core;


import io.netty.channel.Channel;

public class Connection {

    private final String ConnectionName;
    private final Channel channel;

    public String getChannelId() {
        return channelId;
    }

    private final String channelId;
    private final ConnectionRole role;
    private String topic;

    public Connection(String connectionName, Channel channel,
        String channelId, ConnectionRole role) {
        ConnectionName = connectionName;
        this.channel = channel;
        this.channelId = channelId;
        this.role = role;
    }

    public ConnectionRole getRole() {
        return role;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConnectionName() {
        return ConnectionName;
    }


    public Channel getChannel() {
        return channel;
    }

    public enum ConnectionRole {
        Producer,
        Consumer
    }

}
