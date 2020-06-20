package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.broker.PubMessageTask;
import cn.stream2000.railgunmq.core.ProducerMessage;
import io.netty.channel.ChannelHandlerContext;

public class ProducerStrategy {

  public static class PublishMessageStrategy implements MessageStrategyWithBusinessPool {

    @Override
    public void handleMessage(ChannelHandlerContext ctx, Object message) {
      ProducerMessage.PubMessageRequest request = (ProducerMessage.PubMessageRequest) message;

      switch (request.getType())
      {
        case Text:
          System.out.println("本次消息的topic是： "+request.getTopic());

          System.out.println("本次消息的内容是:   "+request.getData().toStringUtf8());
          break;
        case Binary:
          System.out.println("本次消息的topic是： "+request.getTopic());

          System.out.println("本次消息的内容是:   "+request.getData());
          break;
        case Integer:
          System.out.println("本次消息的topic是： "+request.getTopic());

          System.out.println("本次消息的内容是:   "+request.getData());
          break;
        case UNRECOGNIZED:
          System.out.println("无法识别的消息类型");
          break;
      }

      getBusinessPool().submit(new PubMessageTask(request));
    }

  }
}
