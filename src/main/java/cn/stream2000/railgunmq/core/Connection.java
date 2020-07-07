package cn.stream2000.railgunmq.core;


import io.netty.channel.Channel;

public class Connection {

    private final String ConnectionName;
    private final Channel channel;
    private final ConnectionRole role;

    public Connection(String connectionName, Channel channel,
        ConnectionRole role) {
        ConnectionName = connectionName;
        this.channel = channel;
        this.role = role;
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
