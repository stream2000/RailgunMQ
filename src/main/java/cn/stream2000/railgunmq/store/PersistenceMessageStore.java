package cn.stream2000.railgunmq.store;

import cn.stream2000.railgunmq.config.LoggerName;
import cn.stream2000.railgunmq.core.InnerMessage;
import cn.stream2000.railgunmq.helper.SerializeHelper;
import cn.stream2000.railgunmq.store.db.RDB;
import cn.stream2000.railgunmq.store.db.RDBStorePrefix;
import java.nio.charset.StandardCharsets;
import org.rocksdb.ColumnFamilyHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceMessageStore {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.STORE);
    private final RDB rdb;

    public PersistenceMessageStore(RDB rdb) {
        this.rdb = rdb;
    }

    public boolean storeMessage(String topic, String msgId, InnerMessage message) {
        try {
            this.rdb.putSync(columnFamilyHandle(), key(topic, msgId),
                SerializeHelper.serialize(message));
            return true;
        } catch (Exception ex) {
            log.warn("Cache store message failure,cause={}", ex.getCause().toString());
            return false;
        }
    }

    public InnerMessage releaseMessage(String topic, String msgId) {
        byte[] key = key(topic, msgId);
        byte[] value = this.rdb.get(columnFamilyHandle(), key);
        if (value == null) {
            log.warn("The message is not exist,topic={},msgId={}", topic, msgId);
            return null;
        }
        InnerMessage ret = SerializeHelper.deserialize(value, InnerMessage.class);
        this.rdb.delete(columnFamilyHandle(), key);
        return ret;
    }

    private byte[] keyPrefix(String clientId) {
        return (RDBStorePrefix.PERSISTENCE_MESSAGE + clientId).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] key(String topic, String msgId) {
        return (RDBStorePrefix.PERSISTENCE_MESSAGE + topic + msgId)
            .getBytes(StandardCharsets.UTF_8);
    }

    private ColumnFamilyHandle columnFamilyHandle() {
        return this.rdb.getColumnFamilyHandle(RDBStorePrefix.PERSISTENCE_MESSAGE);
    }
}
