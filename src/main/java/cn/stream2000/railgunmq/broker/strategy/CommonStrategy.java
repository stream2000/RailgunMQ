package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.core.Connection;
import cn.stream2000.railgunmq.core.Connection.ConnectionRole;
import cn.stream2000.railgunmq.core.ConnectionMap;
import io.netty.channel.ChannelHandlerContext;

public class CommonStrategy {

    public static class DisconnectStrategy implements MessageStrategyWithBusinessPool {

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {
            //如果连接断开
            ctx.channel().disconnect();
            if (ConnectionMap.getConnection(ctx.channel().id().asLongText()) != null) {
                ConnectionMap.deleteConnection(ctx.channel().id().asLongText());
            }
            deleteSubscription(ctx);

        }

        public static void deleteSubscription(ChannelHandlerContext ctx) {
            Connection conn = ConnectionMap.getConnection(ctx.channel().id().asLongText());
            if (conn != null) {
                if (conn.getRole().equals(ConnectionRole.Consumer)) {
                    String topicName = conn.getTopic();
                    assert topicName != null;
                    Topic topic = TopicManager.getTopic(topicName);
                    if (topic != null) {
                        topic.removeSubscription(ctx.channel().id().asLongText());
                    }
                }
            }
        }
    }
}
