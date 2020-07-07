package cn.stream2000.railgunmq.consumer.spring.controller;

import cn.stream2000.railgunmq.consumer.spring.service.ConsumerService;
import cn.stream2000.railgunmq.core.ConsumerMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public void connect(@RequestParam(value = "topic")String topic,@RequestParam(value = "name") String name) {
        try{
             ConsumerService.connect(topic,name);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/consumer/disconnect")
    @ResponseBody
    public boolean disconnect(@RequestParam(value = "name")String name){
        return ConsumerService.disconnect(name);

    }


    @PostMapping("/consumer/getMessages")
    @ResponseBody
    public List<String> getMessages(@RequestParam(value = "name")String name,@RequestParam(value ="maxTime")int maxTime){
        try{
            return ConsumerService.getMessages(name,maxTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/consumer/getMessage")
    @ResponseBody
    public String getMessage(@RequestParam(value = "name")String name){
        try{
            return ConsumerService.getMessage(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


}