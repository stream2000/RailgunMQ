package cn.stream2000.railgunmq.broker.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RailgunMQApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(cn.stream2000.railgunmq.broker.server.RailgunMQApplication.class);
        app.run(args);
    }

}
