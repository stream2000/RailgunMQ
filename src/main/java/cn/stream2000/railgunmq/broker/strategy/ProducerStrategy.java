package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.broker.PubMessageTaskFactory;
import cn.stream2000.railgunmq.core.ChannelMap;
import cn.stream2000.railgunmq.core.Message;
import cn.stream2000.railgunmq.core.ProducerMessage;
import io.netty.channel.ChannelHandlerContext;

public class ProducerStrategy {

    public static class PublishMessageStrategy implements MessageStrategyWithBusinessPool {

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {
            try{
                ProducerMessage.PubMessageRequest request = (ProducerMessage.PubMessageRequest) message;
                /*System.out.println("channel id 为："+ctx.channel().id().asLongText());
                System.out.println("消息内保存的channel id 为："+request.getChannelId());*/
                switch (request.getType())
                {

                    case Text:
                        System.out.println("本次消息的topic是： "+request.getTopic()+"       消息的id为："+request.getLetterId()    );
                        System.out.println("本次消息的内容是:   "+request.getData().toStringUtf8());
                        break;
                    case Binary:
                        System.out.println("本次消息的topic是： "+request.getTopic()+"       消息的id为："+request.getLetterId()    );
                        System.out.println("本次消息的内容是:   "+request.getData().toStringUtf8());
                        break;
                    case Integer:
                        System.out.println("本次消息的topic是： "+request.getTopic()+"       消息的id为："+request.getLetterId()    );
                        System.out.println("本次消息的内容是:   "+request.getData().toStringUtf8());
                        break;
                    case UNRECOGNIZED:
                        System.out.println("无法识别的消息类型");
                        break;
                }

                getBusinessPool().submit(PubMessageTaskFactory.newPubMessageTask(request));



            }catch (Exception e)
            {
                ProducerMessage.PubMessageAck ack= ProducerMessage.PubMessageAck.newBuilder().setError(Message.ErrorType.InternalServerError).setErrorMessage(e.getMessage()).build();
                ctx.channel().writeAndFlush(ack);

            }

        }

    }

    public static class SetNameStrategy implements MessageStrategyWithBusinessPool{

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {

            try {
                ProducerMessage.SetChannelName request=(ProducerMessage.SetChannelName)message;
                if(request.getNewname()!=null&&request.getNewname()!="")//修改的Name不为空
                {
                    ChannelMap.SetName(request.getChannelId(),request.getNewname());
                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    public static  class DisconnectStrategy implements MessageStrategyWithBusinessPool{

        @Override
        public void handleMessage(ChannelHandlerContext ctx, Object message) {


            ctx.channel().disconnect();
            System.out.println("剩余连接数："+ChannelMap.getNum());
            if(ChannelMap.getChannel(ctx.channel().id().asLongText())!=null)
            {
                System.out.println("删除的连接名为："+ChannelMap.getName(ctx.channel().id().asLongText()));
                ChannelMap.deleteChannel(ctx.channel().id().asLongText());

                System.out.println("剩余连接数："+ChannelMap.getNum());
            }



            try {
                ProducerMessage.Disconnect request=(ProducerMessage.Disconnect)message;
                ChannelMap.deleteChannel(request.getChannelId());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
