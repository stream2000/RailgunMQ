package cn.stream2000.railgunmq.broker.server.spring.controller;

import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.core.Connection;
import cn.stream2000.railgunmq.core.Connection.ConnectionRole;
import cn.stream2000.railgunmq.core.ConnectionMap;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelController {

    @GetMapping("/channel/getAll")
    @ResponseBody
    public List<Connection> getAllChannel() {
        return ConnectionMap.getAll();
    }

    @GetMapping("/channel/get")
    @ResponseBody
    public Connection getChannel(String key) {
        return ConnectionMap.getConnection(key);
    }

    @GetMapping("/channel/delete")
    @ResponseBody
    public void deleteChannel(String key) {
        Connection conn = ConnectionMap.getConnection(key);
        if (conn == null) {
            return;
        }
        ConnectionMap.deleteConnection(conn.getChannelId());
        conn.getChannel().disconnect();
        if (conn.getRole().equals(ConnectionRole.Consumer)) {
            String topicName = conn.getTopic();
            assert topicName != null;
            Topic topic = TopicManager.getTopic(topicName);
            if (topic != null) {
                topic.removeSubscription(conn.getChannelId());
            }
        }
    }

}
