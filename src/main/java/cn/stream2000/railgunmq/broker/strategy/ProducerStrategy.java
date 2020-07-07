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

                        //处理断开连接请求
                        if(request.getTopic().equals("Disconnect"))
                        {
                            ctx.channel().disconnect();
                            System.out.println("剩余连接数："+ChannelMap.getNum());
                            if(ChannelMap.getChannel(ctx.channel().id().asLongText())!=null)
                            {
                                System.out.println("删除的连接名为："+ChannelMap.getName(ctx.channel().id().asLongText()));
                                ChannelMap.deleteChannel(ctx.channel().id().asLongText(),ctx.channel().hashCode());

                                System.out.println("剩余连接数："+ChannelMap.getNum());
                            }
                        }

                        //处理重命名channel请求
                        if(request.getTopic().equals("Rename"))
                        {
                            if(request.getData()!=null&&request.getData().toStringUtf8()!="")//修改的Name不为空
                            {
                                ChannelMap.SetName(ctx.channel().id().asLongText(),request.getData().toStringUtf8());
                            }
                        }
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
}
