package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.core.Connection;
import cn.stream2000.railgunmq.core.ConnectionMap;
import cn.stream2000.railgunmq.core.ProducerAckQueue;
import cn.stream2000.railgunmq.core.ProducerMessage;
import java.util.concurrent.Callable;

public class SendAckController implements Callable<Void> {

    private volatile boolean stopped = false;

    public void stop() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }

    @Override
    public Void call() throws Exception {
        while (!stopped) {
            ProducerMessage.PubMessageAck ack = ProducerAckQueue.getAck();
            if (ConnectionMap.getNum() == 0) {
                System.out.println("ChannelMap为空,可能已经断开连接");
            }
            Connection chan = ConnectionMap.getConnection(ack.getChannelId());
            if (chan == null) {
                throw new ChannelNotExistsException();
            }
            chan.getChannel().writeAndFlush(ack);
        }
        return null;
    }

    static class ChannelNotExistsException extends Exception {

    }
}
