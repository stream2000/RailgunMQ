package cn.stream2000.railgunmq.core;

import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelMap {

  private static final Map<String, Channel> channels = new ConcurrentHashMap<>();

  public static Channel getChannel(String key) {
    return channels.get(key);
  }

  public static void addChannel(String key, Channel channel) {
    channels.put(key, channel);
  }
}
