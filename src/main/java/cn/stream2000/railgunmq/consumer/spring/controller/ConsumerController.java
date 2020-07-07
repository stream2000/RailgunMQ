package cn.stream2000.railgunmq.consumer.spring.controller;

import cn.stream2000.railgunmq.consumer.spring.service.ConsumerService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: RailgunMQ
 * @description:
 * @author: Yu Liu
 * @create: 2020/07/07
 **/
@CrossOrigin(origins = "*", allowCredentials = "true")
@RestController
public class ConsumerController {

    @PostMapping("/consumer/connect")
    @ResponseBody
    public Map<String, String> connect(@RequestParam(value = "topic") String topic,
        @RequestParam(value = "name") String name) {
        try {
            String id = ConsumerService.connect(topic, name);
            HashMap<String, String> result = new HashMap<>();
            result.put("id", id);
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @GetMapping("/consumer/disconnect")
    @ResponseBody
    public boolean disconnect(@RequestParam(value = "name") String name) {
        return ConsumerService.disconnect(name);

    }


    @PostMapping("/consumer/getMessages")
    @ResponseBody
    public List<String> getMessages(@RequestParam(value = "name") String name,
        @RequestParam(value = "maxTime") int maxTime) {
        try {
            return ConsumerService.getMessages(name, maxTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/consumer/getMessage")
    @ResponseBody
    public String getMessage(@RequestParam(value = "name") String name) {
        try {
            return ConsumerService.getMessage(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


}