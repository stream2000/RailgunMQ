package cn.stream2000.railgunmq.broker;

import cn.stream2000.railgunmq.core.ProducerAckQueue;
import cn.stream2000.railgunmq.core.ProducerMessage;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class PubMessageTask implements Callable<Void> {

  private final ProducerMessage.PubMessageRequest request;

  public PubMessageTask(ProducerMessage.PubMessageRequest request) {
    this.request = request;
  }

  @Override
  public Void call() throws Exception {
    TimeUnit.MILLISECONDS.sleep(1000);
    ProducerMessage.PubMessageAck ack = ProducerMessage.PubMessageAck.newBuilder()
        .setLetterId(request.getLetterId())
        .setChannelId(request.getChannelId())
        .build();
    ProducerAckQueue.pushAck(ack);
    return null;
  }
}
