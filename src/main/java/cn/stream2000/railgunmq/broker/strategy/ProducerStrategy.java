package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.broker.PubMessageTaskFactory;
import cn.stream2000.railgunmq.common.config.LoggerName;
import cn.stream2000.railgunmq.core.Connection.ConnectionRole;
import cn.stream2000.railgunmq.core.ConnectionMap;
import cn.stream2000.railgunmq.core.ProducerMessage;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerStrategy {

    public static class PublishMessageStrategy implements MessageStrategyWithBusinessPool {

        private final Logger log = LoggerFactory.getLogger(LoggerName.BROKER);

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {
            ProducerMessage.PubMessageRequest request = (ProducerMessage.PubMessageRequest) message;
            log.info("{}", request.getData().toStringUtf8());
            getBusinessPool().submit(PubMessageTaskFactory.newPubMessageTask(request));
        }

    }

    public static class SetNameStrategy implements MessageStrategyWithBusinessPool {

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {

            ProducerMessage.SetChannelName request = (ProducerMessage.SetChannelName) message;
            if (!StringUtils.isEmpty(request.getNewname()))//修改的Name不为空
            {
                ConnectionMap.addConnection(ctx.channel().id().asLongText(),
                    request.getNewname(), ctx.channel(), ConnectionRole.Consumer);
            }
        }
    }


}
