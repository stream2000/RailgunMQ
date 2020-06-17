package cn.stream2000.railgunmq.broker.server;

import cn.stream2000.railgunmq.broker.SendAckController;
import cn.stream2000.railgunmq.common.config.ServerConfig;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BrokerParallelServer {

  protected static int parallel = ServerConfig.getConfig().getParallel();
  protected static final ExecutorService businessPool = Executors.newFixedThreadPool(parallel);

  public void init() {

  }

  public void start() {
    for (int i = 0; i < parallel; i++) {
      businessPool.submit(new SendAckController());
    }
  }

  public void shutdown() {
    businessPool.shutdown();
  }
}
