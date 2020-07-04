package cn.stream2000.railgunmq.core;

import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest;

public class QueueMessage {

    private final boolean isClientRequest;
    private final boolean needAck;
    private final String topic;
    private final String msgId;
    private final String channelId;
    private final int letterId;
    private final int type;
    private final byte[] payload;

    private QueueMessage(boolean isClientRequest,
        boolean needAck, String topic, String msgId, String channelId,
        int letterId,
        int type,
        byte[] payload) {
        this.isClientRequest = isClientRequest;
        this.needAck = needAck;
        this.topic = topic;
        this.msgId = msgId;
        this.channelId = channelId;
        this.letterId = letterId;
        this.type = type;
        this.payload = payload;
    }

    public static QueueMessage buildFromPubMessageRequest(PubMessageRequest request, String msgId,
        boolean needAck) {
        return new QueueMessage(true, needAck, request.getTopic(), msgId,
            request.getChannelId(), request.getLetterId(), request.getType().getNumber(), request.getData().toByteArray());
    }

    public static QueueMessage storedMessageMapper(StoredMessage message) {
        return new QueueMessage(true, false, message.getTopic(), message.getMsgId(),
            null, 0, message.getType(), message.getPayload());
    }

    public boolean isClientRequest() {
        return isClientRequest;
    }

    public boolean isNeedAck() {
        return needAck;
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

    public int getLetterId() {
        return letterId;
    }

    public String getChannelId() {
        return channelId;
    }
}
