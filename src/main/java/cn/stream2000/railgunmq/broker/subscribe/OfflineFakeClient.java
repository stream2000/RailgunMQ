package cn.stream2000.railgunmq.broker.subscribe;

// when a topic becomes active, we start a fake
// client to send its offline messages
// if there exists no offline message, we wait for a second and try it again.
public class OfflineFakeClient implements Runnable {

    @Override
    public void run() {

    }
}
