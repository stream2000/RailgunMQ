package cn.stream2000.railgunmq.producer;

import cn.stream2000.railgunmq.core.ProducerMessage;

import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception{

        //新建两个连接
        RailgunMQConnection railgunMQClient=new RailgunMQConnection("127.0.0.1",9999);
        RailgunMQConnection MQ2=new RailgunMQConnection("127.0.0.1",9999,"我就是连接2");
        //测试一开始就提供Connection名字



        //连接1发送消息
        railgunMQClient.Publish("default","文本内容");
        ProducerMessage.PubMessageAck pubMessageAck= ProducerMessage.PubMessageAck.newBuilder().setErrorMessage("HELLO").build();
        railgunMQClient.Publish("test",pubMessageAck.toByteArray());
        railgunMQClient.Publish("default",5);
        railgunMQClient.SetChannelName("我就是连接1");


        //连接2发送消息

        MQ2.Publish("default","文本内容");


        //取ack
        Thread.sleep(5000);//等待5秒
        List<ProducerMessage.PubMessageAck> acks=railgunMQClient.getAcks(2000);

        List<ProducerMessage.PubMessageAck> acks1=MQ2.getAcks(2000);


        //连接1取ack
        System.out.println("连接1的ack如下：");
        if(acks!=null){
            for (ProducerMessage.PubMessageAck ack : acks) {
                System.out.println("返回类型为："+ack.getError());
                System.out.println("返回信息为："+ack.getErrorMessage());
                System.out.println("对应的消息id为"+ack.getLetterId());
            }
        }

        //连接2取ack
        System.out.println("连接2的ack如下：");
        if(acks1!=null)
        {
            for (ProducerMessage.PubMessageAck ack:acks1) {
                System.out.println("返回类型为："+ack.getError());
                System.out.println("返回信息为："+ack.getErrorMessage());
                System.out.println("对应的消息id为"+ack.getLetterId());
            }
        }

        railgunMQClient.Disconnect();
        MQ2.Disconnect();
    }
}
