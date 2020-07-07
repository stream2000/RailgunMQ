package cn.stream2000.railgunmq.broker.server.spring.controller;

import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.core.ChannelMap;
import cn.stream2000.railgunmq.core.Connection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ChannelController {

    @GetMapping("/channel/getAll")
    @ResponseBody
    public List<String> getAllChannels() {
        Map<String, Connection> channelMap = ChannelMap.getAllChannels();
        List<String> channelName = new ArrayList<String>();
        for(String key: channelMap.keySet()){
            channelName.add(key);
        }
        return channelName;
    }

    @GetMapping("/channel/get")
    @ResponseBody
    public Connection getChannel(String key) {
        return  ChannelMap.getChannel(key);
    }

    @GetMapping("/channel/delete")
    @ResponseBody
    public void deleteChannel(String key) {
        ChannelMap.getChannel(key).getChannel().close();
        ChannelMap.deleteChannel(key);

    }


}
