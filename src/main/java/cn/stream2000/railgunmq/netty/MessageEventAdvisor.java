package cn.stream2000.railgunmq.netty;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MessageEventAdvisor implements MethodInterceptor {

  private final MessageEventProxy proxy;
  private final Object msg;

  public MessageEventAdvisor(MessageEventProxy proxy, Object msg) {
    this.proxy = proxy;
    this.msg = msg;
  }

  public Object invoke(MethodInvocation invocation) throws Throwable {
    proxy.beforeMessage(msg);
    Object obj = invocation.proceed();
    proxy.afterMessage(msg);
    return obj;
  }
}
