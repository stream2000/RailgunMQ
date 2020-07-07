package cn.stream2000.railgunmq.producer.spring.service;

import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.producer.RailgunMQConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: RailgunMQ
 * @description:
 * @author: Yu Liu
 * @create: 2020/07/07
 **/
public class ProducerService {

    private String Host="localhost";
    private int Port=9999;
    private String channelName=null;
    private RailgunMQConnection conn=null;

    private ProducerService (){

    }
    private static class Singleton{
        private final static ProducerService instance=new ProducerService ();
    }
    public static ProducerService getInstance(){
        return Singleton.instance;
    }

    public static void setHost(String host) {
        getInstance().Host = host;
    }

    public static void setPort(int port) {
        getInstance().Port = port;
    }
    public static void connect() throws InterruptedException {
        if(getInstance().channelName==null)
            getInstance().conn=new RailgunMQConnection(getInstance().Host,getInstance().Port);
        else
            getInstance().conn=new RailgunMQConnection(getInstance().Host,getInstance().Port,getInstance().channelName);
        ProducerMessage.PubMessageAck pubMessageAck= ProducerMessage.PubMessageAck.newBuilder().setErrorMessage("HELLO").build();

    }
    public static void setChannelName(String name){
        if(getInstance().conn==null)
            getInstance().channelName=name;
        else
            getInstance().conn.SetChannelName(name);
    }
    public static void publish(String topic,String content){
        getInstance().conn.Publish(topic,content);

    }

    public static void publish(String topic,int content){
        getInstance().conn.Publish(topic,content);

    }

    public static void publish(String topic,byte[] content){
        getInstance().conn.Publish(topic,content);

    }

    public static List<Map> getACK() throws InterruptedException {
        List<Map> acks=new ArrayList<>();
        while (getInstance().conn.blockingQueue.size()!=0)
        {
            Map ackMap=new HashMap<>();
            ProducerMessage.PubMessageAck ack= getInstance().conn.blockingQueue.take();
            System.out.println("返回类型为："+ack.getError());
            ackMap.put("type","返回类型为"+ack.getError());
            System.out.println("返回信息为："+ack.getErrorMessage());
            ackMap.put("message","返回消息为："+ack.getErrorMessage());
            System.out.println("对应的消息id为"+ack.getLetterId());
            ackMap.put("id","对应的消息id为："+ack.getLetterId());
            acks.add(ackMap);
        }
        return acks;
    }
    public static void disconnect(){
        getInstance().conn.Disconnect();
    }


}
