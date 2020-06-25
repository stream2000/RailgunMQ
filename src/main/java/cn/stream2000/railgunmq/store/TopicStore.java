package cn.stream2000.railgunmq.store;

import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.store.db.RDB;
import cn.stream2000.railgunmq.store.db.RDBStorePrefix;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.rocksdb.ColumnFamilyHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicStore {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.STORE);
    private final RDB rdb;

    public TopicStore(RDB rdb) {
        this.rdb = rdb;
    }

    public boolean addTopic(String topic) {
        try {
            this.rdb.putSync(columnFamilyHandle(), key(topic),
                new byte[]{1});
            return true;
        } catch (Exception ex) {
            log.warn("add topic failure,cause={}", ex.getCause().toString());
            return false;
        }
    }

    public boolean checkTopic(String topic) {
        byte[] key = key(topic);
        byte[] value = this.rdb.get(columnFamilyHandle(), key);
        return value != null;
    }

    public boolean deleteTopic(String topic) {
        byte[] key = key(topic);
        return this.rdb.delete(columnFamilyHandle(), key);
    }

    public List<Topic> getAllTopics() {
        byte[] startKey = key("");
        List<Topic> topics = new ArrayList<>();
        var kvs = rdb.enumerate(columnFamilyHandle(), startKey);
        for(var kv: kvs){
            String topic = new String(kv.getLeft(), StandardCharsets.UTF_8);
            topics.add(new Topic(topic));
        }
        return topics;
    }


    private byte[] keyPrefix(String topic) {
        return (RDBStorePrefix.TOPIC_STORE + topic).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] key(String topic) {
        return (RDBStorePrefix.TOPIC_STORE + topic).getBytes(StandardCharsets.UTF_8);
    }

    private ColumnFamilyHandle columnFamilyHandle() {
        return this.rdb.getColumnFamilyHandle(RDBStorePrefix.TOPIC_STORE);
    }
}
