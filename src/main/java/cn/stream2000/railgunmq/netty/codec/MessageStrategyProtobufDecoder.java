package cn.stream2000.railgunmq.netty.codec;

import cn.stream2000.railgunmq.netty.MessageStrategy;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class MessageStrategyProtobufDecoder extends BaseProtobufDecoder {

    public MessageStrategyProtobufDecoder(ProtoRouter router) {
        super(router);
    }

    @Override
    protected final void handleRawMessage(ChannelHandlerContext ctx, List<Object> out, int dataType,
                                          Object msg) {
        MessageStrategy handler = router.getHandler((byte) dataType);
        MessageStrategyContext messageStrategyContext = new MessageStrategyContext(msg, handler, ctx);
        out.add(messageStrategyContext);
    }
}
