package cn.stream2000.railgunmq.broker.server.spring.controller;

import cn.stream2000.railgunmq.broker.subscribe.Subscription;
import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.core.Connection;
import cn.stream2000.railgunmq.core.ConnectionMap;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowCredentials = "true")
@RestController
public class TopicController {

    @GetMapping("/topics")
    @ResponseBody
    public List<Topic> getTopics() {
        List<Topic> topics = new ArrayList<>();
        List<String> topicNames = TopicManager.getAll();
        for (String topicName : topicNames) {
            Topic topic = TopicManager.getTopic(topicName);
            topics.add(topic);
        }
        return topics;
    }

    @GetMapping("/topic")
    @ResponseBody
    public Topic getTopic(@RequestParam(value = "topic") String topic) {
        return TopicManager.getTopic(topic);

    }

    @PostMapping("/addTopic")
    @ResponseBody
    public boolean addTopic(@RequestParam(value = "topic") String topic) {
        TopicManager.addTopic(topic);
        if (TopicManager.getTopic(topic).getTopicName() == topic) {
            return true;
        } else {
            return false;
        }

    }

    @DeleteMapping("/deleteTopic")
    @ResponseBody
    public boolean deleteTopic(@RequestParam(value = "topic") String topic) {
        TopicManager.deleteTopic(topic);
        if (TopicManager.getTopic(topic) == null) {
            return true;
        } else {
            return true;
        }

    }

    @GetMapping("/topic/all")
    @ResponseBody
    public List<Connection> getAllConn(@RequestParam(value = "topic") String topic) {
        Topic topic1 = TopicManager.getTopic(topic);
        List<Connection> conns = new ArrayList<>();
        if (topic1 != null) {
            List<Subscription> subs = topic1.getAllSubscription();
            for (Subscription sub : subs) {
                Connection conn = ConnectionMap.getConnection(sub.getClientId());
                if (conn != null) {
                    conns.add(conn);
                }
            }
        }
        return conns;
    }
}
