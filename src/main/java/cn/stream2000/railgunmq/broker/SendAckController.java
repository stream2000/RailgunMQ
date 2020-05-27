package cn.stream2000.railgunmq.broker;

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

    @Override public Void call() throws Exception {
        while (!stopped){
            ProducerMessage.PubMessageAck ack = ProducerAckQueue.getAck();
            ack.getChannelId();
        }
        return null;
    }
}
