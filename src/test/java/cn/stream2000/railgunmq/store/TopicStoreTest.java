package cn.stream2000.railgunmq.store;

import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.common.config.StoreConfig;
import cn.stream2000.railgunmq.store.db.RDB;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TopicStoreTest {

    private static RDB rdb;
    private static TopicStore topicStore;

    @BeforeAll
    static void init() {
        StoreConfig storeConfig = new StoreConfig();
        rdb = new RDB(storeConfig);
        rdb.init();
        topicStore = new TopicStore(rdb);
        TopicManager.setup(topicStore);
    }

    @Test
    void testTopic() {
        for (Topic topic : topicStore.getAllTopics()) {
            System.out.println(topic.getTopicName());
        }
        Assert.assertNotNull(TopicManager.getTopic("default"));
        Assert.assertNotNull(TopicManager.getTopic("error"));
        TopicManager.addTopic("test");
        Assert.assertNotNull(TopicManager.getTopic("test"));
        TopicManager.deleteTopic("test");
        Assert.assertNull(TopicManager.getTopic("test"));
    }

}