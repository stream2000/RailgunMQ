package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.core.ChannelMap;
import cn.stream2000.railgunmq.core.ProducerAckQueue;
import cn.stream2000.railgunmq.core.ProducerMessage;
import io.netty.channel.Channel;

import java.util.concurrent.Callable;

public class SendAckController implements Callable<Void> {
    private volatile boolean stopped = false;

    public void stop() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }

    @Override public Void call() throws Exception {
        while (!stopped) {
            ProducerMessage.PubMessageAck ack = ProducerAckQueue.getAck();
            Channel chan = ChannelMap.getChannel(ack.getChannelId());
            if (chan == null) {
                throw new ChannelNotExistsException();
            }
            chan.writeAndFlush(ack);
        }
        return null;
    }

    static class ChannelNotExistsException extends Exception {

    }
}
