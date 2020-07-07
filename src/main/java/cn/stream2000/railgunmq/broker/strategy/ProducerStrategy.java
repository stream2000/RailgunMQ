package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.broker.PubMessageTaskFactory;
import cn.stream2000.railgunmq.core.Connection.ConnectionRole;
import cn.stream2000.railgunmq.core.ConnectionMap;
import cn.stream2000.railgunmq.core.ProducerMessage;
import io.netty.channel.ChannelHandlerContext;

public class ProducerStrategy {

    public static class PublishMessageStrategy implements MessageStrategyWithBusinessPool {

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {
            ProducerMessage.PubMessageRequest request = (ProducerMessage.PubMessageRequest) message;
            getBusinessPool().submit(PubMessageTaskFactory.newPubMessageTask(request));
        }

    }

    public static class SetNameStrategy implements MessageStrategyWithBusinessPool {

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {

            ProducerMessage.SetChannelName request = (ProducerMessage.SetChannelName) message;
            if (request.getNewname() != null && !request.getNewname().equals(""))//修改的Name不为空
            {
                ConnectionMap.addConnection(ctx.channel().id().asLongText(),
                    request.getNewname(), ctx.channel(), ConnectionRole.Consumer);
            }
        }
    }

    public static class DisconnectStrategy implements MessageStrategyWithBusinessPool {

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {

            ctx.channel().disconnect();
            if (ConnectionMap.getConnection(ctx.channel().id().asLongText()) != null) {
                ConnectionMap.deleteConnection(ctx.channel().id().asLongText());
                System.out.println("剩余连接数：" + ConnectionMap.getNum());
            }

            ProducerMessage.Disconnect request = (ProducerMessage.Disconnect) message;
            ConnectionMap.deleteConnection(request.getChannelId());
        }
    }
}
