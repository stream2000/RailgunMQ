package cn.stream2000.railgunmq.broker.strategy;

import cn.stream2000.railgunmq.broker.BusinessTaskExecutor;
import cn.stream2000.railgunmq.netty.MessageStrategy;

import java.util.concurrent.ExecutorService;

public interface MessageStrategyWithBusinessPool extends MessageStrategy {

    default ExecutorService getBusinessPool() {
        return BusinessTaskExecutor.getBusinessPool();
    }
}
