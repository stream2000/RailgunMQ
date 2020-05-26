package cn.stream2000.railgunmq.broker.server;

import cn.stream2000.railgunmq.config.ServerConfig;

import java.util.concurrent.*;

public class BrokerParallelServer {
    protected static final ExecutorService businessPool = newBlockingExecutorsUseCallerRun(16);
    protected int parallel = ServerConfig.getConfig().getParallel();

    public static ExecutorService newBlockingExecutorsUseCallerRun(int size) {
        return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
            (r, executor) -> {
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public void init() {

    }

    public void start() {

    }

    public void shutdown() {
        businessPool.shutdown();
    }
}
