package cn.stream2000.railgunmq.netty;

public interface MessageEventProxy {

  void beforeMessage(Object msg);

  void afterMessage(Object msg);
}
