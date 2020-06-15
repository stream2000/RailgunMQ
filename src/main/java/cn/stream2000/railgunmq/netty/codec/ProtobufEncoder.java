package cn.stream2000.railgunmq.netty.codec;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 参考ProtobufVarint32LengthFieldPrepender 和 ProtobufEncoder
 */
@Sharable
public class ProtobufEncoder extends MessageToByteEncoder<MessageLite> {

  private final ProtoRouter router;

  public ProtobufEncoder(ProtoRouter router) {
    this.router = router;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {

    byte[] body = msg.toByteArray();
    byte[] header = encodeHeader(msg, (short) body.length);
    out.writeBytes(header);
    out.writeBytes(body);
  }

  private byte[] encodeHeader(MessageLite msg, short bodyLength) {
    Byte messageType = router.getMessageTypeMap().get(msg.getClass());
    // TODO check when type = 0
    byte[] header = new byte[4];
    header[0] = (byte) (bodyLength & 0xff);
    header[1] = (byte) ((bodyLength >> 8) & 0xff);
    header[2] = 0; // 保留字段
    header[3] = messageType;
    return header;
  }
}
