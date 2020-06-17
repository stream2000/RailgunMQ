package cn.stream2000.railgunmq.common.config;

import lombok.Data;
import org.yaml.snakeyaml.Yaml;

@Data
public class ServerConfig {

  private static ServerConfig config =
      new Yaml().loadAs(ServerConfig.class.getClassLoader().getResourceAsStream("server.yml"),
          ServerConfig.class);
  private Integer port;
  private Integer parallel;
  private String addr;

  public static ServerConfig getConfig() {
    return config;
  }
}
