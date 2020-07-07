package cn.stream2000.railgunmq.producer;

import cn.stream2000.railgunmq.core.ProducerMessage;

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
        railgunMQClient.Disconnect();


        //连接2发送消息

        MQ2.Publish("default","文本内容");
        MQ2.Disconnect();
    }
}
