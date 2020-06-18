package cn.stream2000.railgunmq.store;

import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.store.db.RDB;
import cn.stream2000.railgunmq.store.db.RDBStorePrefix;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.rocksdb.ColumnFamilyHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// support add and batch gets
public class OfflineMessageStore {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.STORE);
    private final RDB rdb;

    public OfflineMessageStore(RDB rdb) {
        this.rdb = rdb;
    }

    public boolean addMessage(String topic, String msgId) {
        try {
            this.rdb.putSync(columnFamilyHandle(), key(topic, msgId),
                new byte[]{1});
            return true;
        } catch (Exception ex) {
            log.warn("Cache store message failure,cause={}", ex.getCause().toString());
            return false;
        }
    }

    public boolean checkMessage(String topic, String msgId) {
        byte[] key = key(topic, msgId);
        byte[] value = this.rdb.get(columnFamilyHandle(), key);
        return value != null;
    }

    public boolean deleteMessage(String topic, String msgId) {
        byte[] key = key(topic, msgId);
        return this.rdb.delete(columnFamilyHandle(), key);
    }

    public Pair<List<byte[]>, String> getMessages(String topic,String startId, int expect) {
        byte[] startKey = key(topic , startId);
        return rdb.getRange(columnFamilyHandle(), startKey, expect);
    }

    private byte[] keyPrefix(String topic) {
        return (RDBStorePrefix.PERSISTENCE_MESSAGE + topic).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] key(String topic, String msgId) {
        return (RDBStorePrefix.UN_ACK_MESSAGE + topic + msgId)
            .getBytes(StandardCharsets.UTF_8);
    }

    private ColumnFamilyHandle columnFamilyHandle() {
        return this.rdb.getColumnFamilyHandle(RDBStorePrefix.UN_ACK_MESSAGE);
    }
}
