package cn.stream2000.railgunmq.producer.spring.controller;

import cn.stream2000.railgunmq.broker.subscribe.Topic;
import cn.stream2000.railgunmq.broker.subscribe.TopicManager;
import cn.stream2000.railgunmq.core.ProducerMessage;
import cn.stream2000.railgunmq.producer.spring.service.ProducerService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public String connect() {
        try{
            return ProducerService.connect();

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping("/producer/connect")
    @ResponseBody
    public String connect(String id) {
        try{
            return ProducerService.connect(id);

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }

    @PostMapping("/producer/setChannelName")
    @ResponseBody
    public boolean setChannelName(@RequestParam(value = "id")String id,@RequestParam(value = "name")String name){
        return ProducerService.setChannelName(id,name);
    }

    @PostMapping("/producer/publish/string")
    @ResponseBody
    public boolean publish(@RequestParam(value = "id")String id,@RequestParam(value = "topic")String topic,@RequestParam(value = "content")String content){
        return ProducerService.publish(id,topic,content);
    }
    @PostMapping("/producer/publish/int")
    @ResponseBody
    public boolean publish(@RequestParam(value = "id")String id,@RequestParam(value = "topic")String topic,@RequestParam(value = "content")int content){
        return ProducerService.publish(id,topic,content);
    }

    @PostMapping("/producer/publish/bytes")
    @ResponseBody
    public boolean publish(@RequestParam(value = "id")String id,@RequestParam(value = "topic")String topic,@RequestParam(value = "content")byte[] content){
        return ProducerService.publish(id,topic,content);
    }

    @GetMapping("/producer/disconnect")
    @ResponseBody
    public boolean disconnect(@RequestParam(value = "id")String id){
        return ProducerService.disconnect(id);

    }

    @GetMapping("/producer/acks")
    @ResponseBody
    public List<Map> getACK(@RequestParam(value = "id")String id){
        try{
            return ProducerService.getACK(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }
}