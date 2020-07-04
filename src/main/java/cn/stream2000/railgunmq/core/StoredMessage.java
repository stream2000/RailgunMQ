package cn.stream2000.railgunmq.core;

public class StoredMessage {

    private final String topic;
    private final String msgId;
    private final int type;
    private final byte[] payload;

    public StoredMessage(String topic, String msgId, int type, byte[] payload) {
        this.topic = topic;
        this.msgId = msgId;
        this.type = type;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public String getMsgId() {
        return msgId;
    }

    public int getType() {
        return type;
    }

    public byte[] getPayload() {
        return payload;
    }
}
