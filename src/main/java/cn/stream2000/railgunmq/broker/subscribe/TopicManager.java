package cn.stream2000.railgunmq.broker.subscribe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManager {

    private static final TopicManager topicManager = new TopicManager();
    private final Map<String, Topic> topicMap = new ConcurrentHashMap<>();

    public static TopicManager getInstance() {
        return topicManager;
    }

    public Topic getTopic(String topic) {
        return topicMap.get(topic);
    }

    public void addTopic(Topic topic) {
        topicMap.put(topic.getTopicName(), topic);
    }
}
