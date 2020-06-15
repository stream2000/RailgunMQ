package cn.stream2000.railgunmq.broker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BusinessTaskExecutor {

  protected static final ExecutorService businessPool = newBlockingExecutorsUseCallerRun(16);

  public static ExecutorService getBusinessPool() {
    return businessPool;
  }

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
}
