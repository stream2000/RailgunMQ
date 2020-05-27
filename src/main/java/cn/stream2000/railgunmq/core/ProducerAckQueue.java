package cn.stream2000.railgunmq.core;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerAckQueue {
    public static final BlockingQueue<ProducerMessage.PubMessageAck> ackQueue = new LinkedBlockingQueue<>();

    public static void pushAck(ProducerMessage.PubMessageAck ack) throws InterruptedException {
        if (ack == null) {
            return;
        }
        ackQueue.put(ack);
    }

    public static void pushAcks(List<ProducerMessage.PubMessageAck> acks) throws InterruptedException {
        if (acks == null) {
            return;
        }
        for (ProducerMessage.PubMessageAck ack : acks) {
            ackQueue.put(ack);
        }
    }

    public static ProducerMessage.PubMessageAck getAck() throws InterruptedException {
        return ackQueue.take();
    }
}
