package cn.stream2000.railgunmq.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

/**
 * @program: RailgunMQ
 * @description: application for netty client
 * @author: Yu Liu
 * @create: 2020/07/07
 **/
@SpringBootApplication
public class ProducerApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProducerApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8086"));
        app.run(args);
    }

}