package cn.stream2000.railgunmq.broker.server.spring.controller;

import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopicController {

    @GetMapping("topics")
    @ResponseBody
    public List<String> getTopic() {
        return TopicManager.getAll();
    }
}
