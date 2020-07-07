package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.store.TopicStore;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManager {

    private static final
    TopicManager topicManager = new TopicManager();
    private final Map<String, Topic> topicMap = new ConcurrentHashMap<>();
    private TopicStore store;

    public static TopicManager getInstance() {
        return topicManager;
    }

    public static List<String> getAll() {
        return getInstance().store.getAllTopics();
    }

    public static Topic getTopic(String topic) {
        return getInstance().topicMap.get(topic);
    }

    public static void addTopic(String topicName) {
        Topic topic = new Topic(topicName);
        getInstance().store.addTopic(topic.getTopicName());
        getInstance().topicMap.put(topic.getTopicName(), topic);
    }

    // NOTE: delete a topic may cause something unexpected. Make sure you understand what you're going
    // to do.
    public static void deleteTopic(String topicName) {
        Topic topic = new Topic(topicName);
        getInstance().store.deleteTopic(topic.getTopicName());
        getInstance().topicMap.remove(topic.getTopicName());
    }


    public static void setup(TopicStore topicStore) {
        getInstance().store = topicStore;
        List<String> previousTopics = getInstance().store.getAllTopics();
        for (String topic : previousTopics) {
            addTopic(topic);
        }
        if (getTopic("default") == null) {
            getInstance().store.addTopic("default");
            addTopic("default");
        }
        if (getTopic("error") == null) {
            getInstance().store.addTopic("error");
            addTopic("error");
        }
    }
}
