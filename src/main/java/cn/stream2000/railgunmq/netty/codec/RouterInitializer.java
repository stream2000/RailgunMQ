package cn.stream2000.railgunmq.netty.codec;

import cn.stream2000.railgunmq.core.ConsumerMessage;
import cn.stream2000.railgunmq.core.Message;
import cn.stream2000.railgunmq.core.ProducerMessage;

public class RouterInitializer {

  public static ProtoRouter initialize() {
    ProtoRouter router = new ProtoRouter();
    router.register((byte) Message.MessageType.PubMessageRequestType.getNumber(),
        ProducerMessage.PubMessageRequest.getDefaultInstance());
    router.register((byte) Message.MessageType.PubMessageResponseType.getNumber(),
        ProducerMessage.PubMessageAck.getDefaultInstance());
    router.register((byte) Message.MessageType.CreateChannelRequestType.getNumber(),
        ProducerMessage.CreateChannelRequest.getDefaultInstance());
    router.register((byte) Message.MessageType.CreateChannelResponseType.getNumber(),
        ProducerMessage.CreateChannelResponse.getDefaultInstance());
    router.register((byte) Message.MessageType.SetChannelNameType.getNumber(),
            ProducerMessage.SetChannelName.getDefaultInstance());
    router.register((byte) Message.MessageType.DisconnecType.getNumber(),
            ProducerMessage.Disconnect.getDefaultInstance());
    router.register((byte) Message.MessageType.SubMessageRequestType.getNumber(),
            ConsumerMessage.SubMessageRequest.getDefaultInstance());
    router.register((byte) Message.MessageType.SubMessageResponseType.getNumber(),
            ConsumerMessage.SubMessageAck.getDefaultInstance());
    router.register((byte) Message.MessageType.SendMessageResponseType.getNumber(),
            ConsumerMessage.SendMessageAck.getDefaultInstance());
    router.register((byte) Message.MessageType.CreateChannelRequestType.getNumber(),
            ConsumerMessage.CreateChannelRequest.getDefaultInstance());
    router.register((byte) Message.MessageType.CreateChannelResponseType.getNumber(),
            ConsumerMessage.CreateChannelResponse.getDefaultInstance());
    return router;
  }

}
