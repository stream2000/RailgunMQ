package cn.stream2000.railgunmq.broker.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RailgunMQApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RailgunMQApplication.class);
        app.run(args);
    }

}
