package cn.stream2000.railgunmq.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

/**
 * @program: RailgunMQ
 * @description:
 * @author: Yu Liu
 * @create: 2020/07/07
 **/
@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConsumerApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8087"));
        app.run(args);
    }
}