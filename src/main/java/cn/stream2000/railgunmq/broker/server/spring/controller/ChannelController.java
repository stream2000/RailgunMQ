package cn.stream2000.railgunmq.broker.server.spring.controller;

import cn.stream2000.railgunmq.core.Connection;
import cn.stream2000.railgunmq.core.ConnectionMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChannelController {

    @GetMapping("/channel/getAll")
    @ResponseBody
    public List<Connection> getAllChannel() {return ConnectionMap.getAll();}

    @GetMapping("/channel/get")
    @ResponseBody
    public Connection getChannel(String key) {
        return  ConnectionMap.getConnection(key);
    }

    @GetMapping("/channel/delete")
    @ResponseBody
    public void deleteChannel(String key) {
        ConnectionMap.getConnection(key).getChannel().disconnect();
        ConnectionMap.deleteConnection(key);

    }


}
