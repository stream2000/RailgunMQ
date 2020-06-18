package cn.stream2000.railgunmq.store;

import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.InnerMessage;
import cn.stream2000.railgunmq.core.Store.RocksDBMessage;
import cn.stream2000.railgunmq.core.Store.RocksDBMessage.payload_type;
import cn.stream2000.railgunmq.store.db.RDB;
import cn.stream2000.railgunmq.store.db.RDBStorePrefix;
import com.google.protobuf.ByteString;
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

    public boolean storeMessage(InnerMessage message) {
        try {
            RocksDBMessage value = RocksDBMessage.newBuilder().
                setType(payload_type.forNumber(message.getType()))
                .setMsgId(message.getMsgId())
                .setTopic(message.getTopic())
                .setData(ByteString.copyFrom(message.getPayload())).build();
            this.rdb.putSync(columnFamilyHandle(), key(message.getTopic(), message.getMsgId()),
                value.toByteArray());
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
        RocksDBMessage msg;
        try {
            msg = RocksDBMessage.parseFrom(value);
        } catch (Exception ex) {
            log.error("[RocksDB] parse protobuf data error{}", ex.getCause().toString());
            return null;
        }
        this.rdb.delete(columnFamilyHandle(), key);
        InnerMessage ret = new InnerMessage(msg.getTopic(), msg.getMsgId(), msg.getTypeValue(),
            msg.getData().toByteArray());
        return ret;
    }

    private byte[] keyPrefix(String topic) {
        return (RDBStorePrefix.PERSISTENCE_MESSAGE + topic).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] key(String topic, String msgId) {
        return (RDBStorePrefix.PERSISTENCE_MESSAGE + topic + msgId)
            .getBytes(StandardCharsets.UTF_8);
    }

    private ColumnFamilyHandle columnFamilyHandle() {
        return this.rdb.getColumnFamilyHandle(RDBStorePrefix.PERSISTENCE_MESSAGE);
    }
}
