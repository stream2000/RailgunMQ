package cn.stream2000.railgunmq.producer;

import cn.stream2000.railgunmq.core.ProducerMessage;

public class Test {
    public static void main(String[] args) throws Exception{
        RailgunMQClient railgunMQClient=RailgunMQClient.getRailgunMQClientInstance();
        railgunMQClient.Publish("字符串测试","文本内容");
        ProducerMessage.PubMessageAck pubMessageAck= ProducerMessage.PubMessageAck.newBuilder().setErrorMessage("HELLO").build();
        railgunMQClient.Publish("字节流测试",pubMessageAck.toByteArray());

        railgunMQClient.Publish("数字测试",5);
    }
}
