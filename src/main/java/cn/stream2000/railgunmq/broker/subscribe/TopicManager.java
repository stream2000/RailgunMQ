package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.store.TopicStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManager {

    private static final TopicManager topicManager = new TopicManager();
    private final Map<String, Topic> topicMap = new ConcurrentHashMap<>();
    private TopicStore store;

    public static TopicManager getInstance() {
        return topicManager;
    }

    public static Topic getTopic(String topic) {
        return getInstance().topicMap.get(topic);
    }

    public static void addTopic(Topic topic) {
        getInstance().store.addTopic(topic.getTopicName());
        getInstance().topicMap.put(topic.getTopicName(), topic);
    }

    public static void setup(TopicStore topicStore) {
        getInstance().store = topicStore;
        var previousTopics = getInstance().store.getAllTopics();
        for (var topic : previousTopics) {
            addTopic(topic);
        }
        if (getTopic("default") == null) {
            getInstance().store.addTopic("default");
            addTopic(new Topic("default"));
        }
        if (getTopic("error") == null) {
            getInstance().store.addTopic("error");
            addTopic(new Topic("error"));
        }
    }
}
