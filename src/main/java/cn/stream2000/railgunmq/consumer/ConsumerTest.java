package cn.stream2000.railgunmq.consumer;

import cn.stream2000.railgunmq.core.ConsumerMessage.SubMessage;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConsumerTest {

    public static void main(String[] args) throws InterruptedException {
        RailgunMQConsumer consumer = new RailgunMQConsumer("localhost", 9999, "default", "newName");
        List<SubMessage> result = consumer.getMessages(1000);
        for (SubMessage subMessage : result) {
            System.out.println(subMessage.getData().toStringUtf8());
        }
        TimeUnit.MILLISECONDS.sleep(6000);
        result = consumer.getMessages(1000);
        for (SubMessage subMessage : result) {
            System.out.println(subMessage.getData().toStringUtf8());
        }
        System.out.println("here  !!!!!!!");
        TimeUnit.MILLISECONDS.sleep(6000);
        result = consumer.getMessages(1000);
        for (SubMessage subMessage : result) {
            System.out.println(subMessage.getData().toStringUtf8());
        }

    }
}
