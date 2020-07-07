package cn.stream2000.railgunmq.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: RailgunMQ
 * @description: application for netty client
 * @author: Yu Liu
 * @create: 2020/07/07
 **/
@SpringBootApplication
public class RailgunMQApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(cn.stream2000.railgunmq.producer.RailgunMQApplication.class);
        app.run(args);
    }

}