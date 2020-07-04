package cn.stream2000.railgunmq.store;

import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.StoredMessage;
import cn.stream2000.railgunmq.core.Store.RocksDBMessage;
import cn.stream2000.railgunmq.core.Store.RocksDBMessage.payload_type;
import cn.stream2000.railgunmq.store.db.RDB;
import cn.stream2000.railgunmq.store.db.RDBStorePrefix;
import com.google.protobuf.ByteString;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.rocksdb.ColumnFamilyHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceMessageStore {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.STORE);
    private final RDB rdb;

    public PersistenceMessageStore(RDB rdb) {
        this.rdb = rdb;
    }

    public boolean storeMessage(StoredMessage message) {
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

    public StoredMessage releaseMessage(String topic, String msgId) {
        byte[] key = key(topic, msgId);
        RocksDBMessage msg = innerGetMessage(key);
        if (msg == null) {
            log.warn("The message is not exist,topic={},msgId={}", topic, msgId);
            return null;
        }
        this.rdb.delete(columnFamilyHandle(), key);
        StoredMessage ret = new StoredMessage(msg.getTopic(), msg.getMsgId(), msg.getTypeValue(),
            msg.getData().toByteArray());
        return ret;
    }

    public StoredMessage getMessage(String topic, String msgId) {
        byte[] key = key(topic, msgId);
        RocksDBMessage msg = innerGetMessage(key);
        if (msg == null) {
            log.warn("The message is not exist,topic={},msgId={}", topic, msgId);
            return null;
        }
        StoredMessage ret = new StoredMessage(msg.getTopic(), msg.getMsgId(), msg.getTypeValue(),
            msg.getData().toByteArray());
        return ret;
    }


    public RocksDBMessage innerGetMessage(byte[] key) {
        byte[] value = this.rdb.get(columnFamilyHandle(), key);
        if (value == null) {
            return null;
        }
        RocksDBMessage msg;
        try {
            msg = RocksDBMessage.parseFrom(value);
        } catch (Exception ex) {
            log.error("[RocksDB] parse protobuf data error{}", ex.getCause().toString());
            return null;
        }
        return msg;
    }

    public Pair<List<byte[]>, byte[]> getMessages(String topic, String startId, int expect) {
        byte[] startKey = key(topic, startId);
        return rdb.getRange(columnFamilyHandle(), startKey, expect);
    }

    public StoredMessage parseMessage(byte[] value) {
        if (value == null) {
            return null;
        }
        RocksDBMessage msg;
        try {
            msg = RocksDBMessage.parseFrom(value);
            return new StoredMessage(msg.getTopic(), msg.getMsgId(), msg.getTypeValue(),
                msg.getData().toByteArray());
        } catch (Exception ex) {
            log.error("[RocksDB] parse protobuf data error{}", ex.getCause().toString());
            return null;
        }
    }

    public Pair<List<byte[]>, byte[]> getMessages(byte[] startKey, int expect) {
        return rdb.getRange(columnFamilyHandle(), startKey, expect);
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
