package cn.stream2000.railgunmq.consumer.spring.service;

import cn.stream2000.railgunmq.consumer.RailgunMQConsumer;
import cn.stream2000.railgunmq.core.ConsumerMessage;
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
public class ConsumerService {

    private String Host = "localhost";
    private int Port = 9999;
    private String channelName = null;
    private RailgunMQConsumer conn = null;
    private List<String> names = new ArrayList<String>();
    private Map<String, RailgunMQConsumer> conns = new HashMap<>();

    private ConsumerService() {

    }

    public static ConsumerService getInstance() {
        return Singleton.instance;
    }

    public static String connect(String topic, String name) throws InterruptedException {
        ConsumerService cs = ConsumerService.getInstance();
        RailgunMQConsumer rp = new RailgunMQConsumer(getInstance().Host, getInstance().Port, topic,
            name);
        getInstance().names.add(rp.channelId);
        getInstance().conns.put(name, rp);
        return rp.channelId;

    }

    public static boolean disconnect(String name) {
        if (getInstance().names.contains(name)) {
            RailgunMQConsumer rc = getInstance().conns.get(name);
            rc.Disconnect();
            getInstance().conns.remove(name);
            getInstance().names.remove(name);
            return true;
        } else {
            System.out.println("该id未连接。");
            return false;
        }
    }

    public static List<String> getMessages(String name, int maxTime) throws InterruptedException {
        if (getInstance().names.contains(name)) {
            List<String> sl = new ArrayList<>();
            List<ConsumerMessage.SubMessage> list = getInstance().conns.get(name)
                .getMessages(maxTime);
            for (ConsumerMessage.SubMessage sm : list) {
                sl.add(sm.toString());

            }
            return sl;
        } else {
            System.out.println("该id未连接。");
            return null;
        }

    }

    public static String getMessage(String name) throws InterruptedException {
        if (getInstance().names.contains(name)) {
            return getInstance().conns.get(name).getMessage().toString();
        } else {
            System.out.println("该id未连接。");
            return null;
        }
    }

    private static class Singleton {

        private final static ConsumerService instance = new ConsumerService();
    }


}