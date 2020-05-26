package cn.stream2000.railgunmq.netty.codec;

import cn.stream2000.railgunmq.broker.strategy.MessageStrategy;
import com.google.protobuf.GeneratedMessageV3;

import java.util.HashMap;
import java.util.Map;

public class ProtoRouter {
    private Map<Byte, GeneratedMessageV3> typeMessageMap = new HashMap<>();
    private Map<Class, Byte> messageTypeMap = new HashMap<>();
    private Map<Byte, MessageStrategy> handlerMap = new HashMap<>();

    public void register(byte messageType, GeneratedMessageV3 message) {
        typeMessageMap.put(messageType, message);
        messageTypeMap.put(message.getClass(), messageType);
    }

    public void registerHandler(GeneratedMessageV3 msg, MessageStrategy handler) {
        Byte messageType = messageTypeMap.get(msg.getClass());
        if (!typeMessageMap.containsKey(messageType)) {
            return;
        }
        handlerMap.put(messageType, handler);
    }

    public Map<Byte, GeneratedMessageV3> getTypeMessageMap() {
        return typeMessageMap;
    }

    public Map<Class, Byte> getMessageTypeMap() {
        return messageTypeMap;
    }

    public MessageStrategy getHandler(Object obj) {
        Byte messageType = messageTypeMap.get(obj.getClass());
        return handlerMap.get(messageType);
    }

    public MessageStrategy getHandler(Byte messageType) {
        return handlerMap.get(messageType);
    }
}
