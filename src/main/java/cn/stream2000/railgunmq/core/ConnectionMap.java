package cn.stream2000.railgunmq.core;

import cn.stream2000.railgunmq.core.Connection.ConnectionRole;
import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionMap {

    private static final Map<String, Connection> connections = new ConcurrentHashMap<>();//channel的id与channel的对应

    //这里是根据channel的id获取channel，不是Name
    public static Connection getConnection(String key) {
        return connections.get(key);
    }

    public static void addConnection(String key, String name, Channel channel,
        ConnectionRole role) {
        Connection connection = new Connection(name, channel, role);
        connections.put(key, connection);//id-----connection对象
    }

    //根据channel的id删除
    public static void deleteConnection(String channelId) {
        connections.remove(channelId);
    }

}
