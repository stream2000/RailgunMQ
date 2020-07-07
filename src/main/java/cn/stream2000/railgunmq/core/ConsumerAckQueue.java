package cn.stream2000.railgunmq.core;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsumerAckQueue {

    public static final BlockingQueue<ConsumerMessage.SubMessageAck> ackQueue = new LinkedBlockingQueue<>();

    public static void pushAck(ConsumerMessage.SubMessageAck ack) throws InterruptedException {
        if (ack == null) {
            return;
        }
        ackQueue.put(ack);
    }

    public static void pushAcks(List<ConsumerMessage.SubMessageAck> acks)
            throws InterruptedException {
        if (acks == null) {
            return;
        }
        for (ConsumerMessage.SubMessageAck ack : acks) {
            ackQueue.put(ack);
        }
    }

    public static ConsumerMessage.SubMessageAck getAck() throws InterruptedException {
        return ackQueue.take();
    }
}
