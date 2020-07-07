package cn.stream2000.railgunmq.core;

import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelMap {

  private static final Map<String ,String> channelName=new ConcurrentHashMap<>();//channel的id与channel的Name的对应，用户设置channel名字就是设置这个的Name
  private static final Map<String, Connection> channels = new ConcurrentHashMap<>();//channel的id与channel的对应

  //这里是根据channel的id获取channel，不是Name
  public static Connection getChannel(String key) {
    return channels.get(key);
  }

  public static void addChannel(String key, Channel channel) {
    Connection connection=new Connection();
    connection.setChannel(channel);

    connection.setConnectionName(key);//此处ConnectionName是channel的id，因为key现在不是用户设置的
    channelName.put(key,connection.getConnectionName());// id----Name
    channels.put(key, connection);//id-----connection对象
  }

  //根据channel的id删除
  public static void deleteChannel(String channelid)
  {
    channelName.remove(channelid);
    channels.remove(channelid);
  }


//获取channel数量
  public static int getNum()
  {
    return channels.size();
  }

  //根据id获取channel的名字
  public static String getName(String key)
  {
    return channelName.get(key);
  }

  //根据id设置channel的名字
  public static void SetName(String id,String NewName)
  {
    channelName.replace(id,NewName);
  }
}
