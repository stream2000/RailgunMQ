package cn.stream2000.railgunmq.netty.codec;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 参考ProtobufVarint32FrameDecoder 和 ProtobufDecoder
 */

public class BaseProtobufDecoder extends ByteToMessageDecoder {

    protected ProtoRouter router;

    public BaseProtobufDecoder(ProtoRouter router) {
        this.router = router;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() > 4) { // 如果可读长度小于包头长度，退出。
            in.markReaderIndex();

            // 获取包头中的body长度
            byte low = in.readByte();
            byte high = in.readByte();
            short s0 = (short) (low & 0xff);
            short s1 = (short) (high & 0xff);
            s1 <<= 8;
            short length = (short) (s0 | s1);

            // 获取包头中的protobuf类型
            in.readByte();
            byte dataType = in.readByte();

            // 如果可读长度小于body长度，恢复读指针，退出。
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            // 读取body
            ByteBuf bodyByteBuf = in.readBytes(length);

            byte[] array;
            int offset;

            int readableLen = bodyByteBuf.readableBytes();
            if (bodyByteBuf.hasArray()) {
                array = bodyByteBuf.array();
                offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
            } else {
                array = new byte[readableLen];
                bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                offset = 0;
            }
            //反序列化
            MessageLite result = decodeBody(dataType, array, offset, readableLen);
            handleRawMessage(ctx, out, dataType, result);
        }
    }

    protected void handleRawMessage(ChannelHandlerContext ctx, List<Object> out, int dataType,
                                    Object msg) {
        out.add(msg);
    }

    private MessageLite decodeBody(byte dataType, byte[] array, int offset, int length)
            throws Exception {
        return router.getTypeMessageMap().get(dataType).getDefaultInstanceForType().
                getParserForType().parseFrom(array, offset, length); // or throw exception
    }
}
