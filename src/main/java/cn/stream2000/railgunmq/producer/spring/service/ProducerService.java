package cn.stream2000.railgunmq.producer.spring.service;

import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.producer.RailgunMQProducer;

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
    private RailgunMQProducer conn=null;
    private List<String> ids=new ArrayList<String>();
    private Map<String,RailgunMQProducer> conns=new HashMap<>();

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
    public static String connect() throws InterruptedException {
        ProducerService ps=ProducerService.getInstance();
        RailgunMQProducer rc;
        rc=new RailgunMQProducer(getInstance().Host,getInstance().Port,"default");
        String id=rc.getChannelId();
        getInstance().ids.add(id);
        getInstance().conns.put(id,rc);
        ProducerMessage.PubMessageAck pubMessageAck= ProducerMessage.PubMessageAck.newBuilder().setErrorMessage("HELLO").build();
        return id;
    }

    public static String connect(String name) throws InterruptedException {
        ProducerService ps=ProducerService.getInstance();
        RailgunMQProducer rc;
        rc=new RailgunMQProducer(getInstance().Host,getInstance().Port,name);
        String id=rc.getChannelId();
        getInstance().ids.add(id);
        getInstance().conns.put(id,rc);
        ProducerMessage.PubMessageAck pubMessageAck= ProducerMessage.PubMessageAck.newBuilder().setErrorMessage("HELLO").build();
        return id;
    }
    public static boolean setChannelName(String id,String name){
        ProducerService ps=ProducerService.getInstance();
        if (ps.ids.contains(id)){
            RailgunMQProducer rc=ps.conns.get(id);
            rc.SetChannelName(name);
            return true;
        }else {
            System.out.println("该id未连接。");
            return false;
        }

    }
    public static boolean publish(String id,String topic,String content){
        if (getInstance().ids.contains(id)){
            RailgunMQProducer rc=getInstance().conns.get(id);
            rc.Publish(topic,content);
            return true;
        }else {
            System.out.println("该id未连接。");
            return false;
        }


    }

    public static boolean publish(String id,String topic,int content){
        if (getInstance().ids.contains(id)){
            RailgunMQProducer rc=getInstance().conns.get(id);
            rc.Publish(topic,content);
            return true;
        }else {
            System.out.println("该id未连接。");
            return false;
        }

    }

    public static boolean publish(String id,String topic,byte[] content){
        if (getInstance().ids.contains(id)){
            RailgunMQProducer rc=getInstance().conns.get(id);
            rc.Publish(topic,content);
            return true;
        }else {
            System.out.println("该id未连接。");
            return false;
        }

    }

    public static List<Map> getACK(String id) throws InterruptedException {
        if(getInstance().ids.contains(id)){
            RailgunMQProducer rc=getInstance().conns.get(id);
            List<ProducerMessage.PubMessageAck> acks=rc.getAcks(2000);
            List<Map> maps=new ArrayList<>();
            if(acks!=null){
                for (ProducerMessage.PubMessageAck ack : acks) {
                    Map ackMap=new HashMap<>();
                    System.out.println("返回类型为："+ack.getError());
                    System.out.println("返回信息为："+ack.getErrorMessage());
                    System.out.println("对应的消息id为"+ack.getLetterId());
                    ackMap.put("type","返回类型为："+ack.getError());
                    ackMap.put("message","返回消息为："+ack.getErrorMessage());
                    ackMap.put("id","对应的消息id为："+ack.getLetterId());
                    maps.add(ackMap);
                }
            }


            return maps;
        }else {
            System.out.println("该id未连接。");
            return null;
        }

    }
    public static boolean disconnect(String id){
        if(getInstance().ids.contains(id)){
            RailgunMQProducer rc= getInstance().conns.get(id);
            rc.Disconnect();
            getInstance().conns.remove(id);
            getInstance().ids.remove(id);
            return true;
        }
        else {
            System.out.println("该id未连接");
            return false;
        }

    }


}
