package cn.stream2000.railgunmq.producer.spring.controller;

import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.producer.spring.service.ProducerService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: RailgunMQ
 * @description:
 * @author: Yu Liu
 * @create: 2020/07/07
 **/
@CrossOrigin(origins = "*", allowCredentials = "true")
@RestController
public class ProducerController {

    @GetMapping("/producer/connect")
    @ResponseBody
    public boolean connect() {
        try{
            ProducerService.connect();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

    }
    @PostMapping("/producer/setChannelName")
    @ResponseBody
    public void setChannelName(@RequestParam(value = "name")String name){
        ProducerService.setChannelName(name);
    }

    @PostMapping("/producer/publish/string")
    @ResponseBody
    public void publish(@RequestParam(value = "topic")String topic,@RequestParam(value = "content")String content){
        ProducerService.publish(topic,content);
    }
    @PostMapping("/producer/publish/int")
    @ResponseBody
    public void publish(@RequestParam(value = "topic")String topic,@RequestParam(value = "content")int content){
        ProducerService.publish(topic,content);
    }

    @PostMapping("/producer/publish/bytes")
    @ResponseBody
    public void publish(@RequestParam(value = "topic")String topic,@RequestParam(value = "content")byte[] content){
        ProducerService.publish(topic,content);
    }

    @GetMapping("producer/disconnect")
    @ResponseBody
    public void disconnect(){
        ProducerService.disconnect();

    }
}