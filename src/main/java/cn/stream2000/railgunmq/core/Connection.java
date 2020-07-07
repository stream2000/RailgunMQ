package cn.stream2000.railgunmq.core;


import io.netty.channel.Channel;

public class Connection {

    private final String ConnectionName;
    private final Channel channel;
    private final ConnectionRole role;
    private String topic;

    public Connection(String connectionName, Channel channel,
        ConnectionRole role) {
        ConnectionName = connectionName;
        this.channel = channel;
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
