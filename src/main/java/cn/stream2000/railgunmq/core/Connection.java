package cn.stream2000.railgunmq.core;


import io.netty.channel.Channel;

public class Connection {
    public String getConnectionName() {
        return ConnectionName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setConnectionName(String connectionName) {
        ConnectionName = connectionName;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    String ConnectionName;
    Channel channel;


}
