package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.broker.PubMessageTaskFactory;
import cn.stream2000.railgunmq.core.Message;
import cn.stream2000.railgunmq.core.ProducerMessage;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;

public class ProducerStrategy {

    public static class PublishMessageStrategy implements MessageStrategyWithBusinessPool {

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {
            try{
                ProducerMessage.PubMessageRequest request = (ProducerMessage.PubMessageRequest) message;
                switch (request.getType())
                {

                    case Text:
                        System.out.println("本次消息的topic是： "+request.getTopic());
                        Thread.sleep(3000);
                        System.out.println("本次消息的内容是:   "+request.getData().toStringUtf8());
                        break;
                    case Binary:
                        System.out.println("本次消息的topic是： "+request.getTopic());
                        System.out.println("本次消息的内容是:   "+request.getData().toStringUtf8());
                        break;
                    case Integer:
                        System.out.println("本次消息的topic是： "+request.getTopic());
                        System.out.println("本次消息的内容是:   "+request.getData().toStringUtf8());
                        break;
                    case UNRECOGNIZED:
                        System.out.println("无法识别的消息类型");
                        break;
                }

                getBusinessPool().submit(PubMessageTaskFactory.newPubMessageTask(request));


                ProducerMessage.PubMessageAck ack= ProducerMessage.PubMessageAck.newBuilder().setError(Message.ErrorType.OK).setErrorMessage("消息已被Broker接收").build();
                ctx.channel().writeAndFlush(ack);

            }catch (Exception e)
            {
                ProducerMessage.PubMessageAck ack= ProducerMessage.PubMessageAck.newBuilder().setError(Message.ErrorType.InternalServerError).setErrorMessage(e.getMessage()).build();
                ctx.channel().writeAndFlush(ack);

            }

        }

    }
}
