package cn.stream2000.railgunmq.netty;

import java.util.concurrent.ExecutorService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

public class MessageEventWrapper<T> extends ChannelInboundHandlerAdapter
    implements MessageEventHandler, MessageEventProxy {

  final public static String proxyMappedName = "handleMessage";
  protected Throwable cause;
  protected MessageEventWrapper<T> wrapper;
  protected ExecutorService businessPool;

  public void setBusinessPool(ExecutorService businessPool) {
    this.businessPool = businessPool;
  }

  @Override
  public void handleMessage(ChannelHandlerContext channelHandlerContext, Object message)
      throws Exception {

  }

  @Override
  public void beforeMessage(Object msg) {

  }

  @Override
  public void afterMessage(Object msg) {

  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    super.channelRead(ctx, msg);

    // spring aop
    ProxyFactory weaver = new ProxyFactory(wrapper);
    NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
    advisor.setMappedName(MessageEventWrapper.proxyMappedName);
    advisor.setAdvice(new MessageEventAdvisor(wrapper, msg));
    weaver.addAdvisor(advisor);

    MessageEventHandler proxyObject = (MessageEventHandler) weaver.getProxy();
    proxyObject.handleMessage(ctx, msg);
  }



  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    super.channelReadComplete(ctx);
  }

  public Throwable getCause() {
    return cause;
  }

  public void setWrapper(MessageEventWrapper<T> wrapper) {
    this.wrapper = wrapper;
  }
}
