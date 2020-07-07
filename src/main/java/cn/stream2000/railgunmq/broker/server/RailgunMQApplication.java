package cn.stream2000.railgunmq.broker.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class RailgunMQApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(cn.stream2000.railgunmq.broker.server.RailgunMQApplication.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8088"));
        app.run(args);
    }



}
