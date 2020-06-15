package cn.stream2000.railgunmq.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;

public class SemaphoreCache {

  private final static int hookTime = 5;

  private static final LoadingCache<String, Semaphore> cache = CacheBuilder.newBuilder().
      concurrencyLevel(8).
      build(new CacheLoader<String, Semaphore>() {
        @ParametersAreNonnullByDefault
        public Semaphore load(String input) throws Exception {
          return new Semaphore(0);
        }
      });

  public static int getAvailablePermits(String key) {
    try {
      return cache.get(key).availablePermits();
    } catch (ExecutionException ex) {
      Logger.getLogger(Semaphore.class.getName()).log(Level.SEVERE, null, ex);
      return 0;
    }
  }

  public static void release(String key) {
    try {
      // schedule current thread out so that thread that is acquiring this semaphore can run immediately
      cache.get(key).release();
      TimeUnit.MILLISECONDS.sleep(hookTime);
    } catch (ExecutionException | InterruptedException ex) {
      Logger.getLogger(SemaphoreCache.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void acquire(String key) {
    try {
      cache.get(key).acquire();
      //TimeUnit.MILLISECONDS.sleep(hookTime);
    } catch (InterruptedException | ExecutionException ex) {
      Logger.getLogger(SemaphoreCache.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
