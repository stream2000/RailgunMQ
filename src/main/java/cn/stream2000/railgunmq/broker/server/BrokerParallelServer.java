package cn.stream2000.railgunmq.broker.server;

import cn.stream2000.railgunmq.config.ServerConfig;

public class BrokerParallelServer {
    protected static int parallel = ServerConfig.getConfig().getParallel();
    //    protected static final ExecutorService businessPool = Executors.newFixedThreadPool(parallel);

    public void init() {

    }

    public void start() {

    }

    public void shutdown() {
        //        businessPool.shutdown();
    }
}
