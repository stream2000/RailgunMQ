package cn.stream2000.railgunmq.config;

import lombok.Data;
import org.yaml.snakeyaml.Yaml;

@Data public class ServerConfig {
    private Integer port;
    private Integer parallel;
    private String addr;

    private static ServerConfig config =
        new Yaml().loadAs(ServerConfig.class.getClassLoader().getResourceAsStream("server.yml"), ServerConfig.class);
    public static ServerConfig getConfig() {
        return config;
    }
}
