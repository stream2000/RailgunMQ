# 开发手册

## 1. 消息与路由

### 1.1 消息定义

使用protobuf工具生成消息类，存放在core包中。 

### 1.2 消息注册

使用消息之前要先注册。
注册方式为
1. 在message.proto中定义消息的枚举
2. 重新生成Message.java
3. 在netty/codec包下的RouterInitializer中注册消息，具体方式可参照示例。 

### 1.3 处理函数定义

参照broker/strategy/ProducerStrategy， 定义一个实现了MessageStrategy接口的策略类。

### 1.4 处理函数注册，与消息相关联

客户端和服务端都有注册消息的示例， 基本是在netty的Initializer中进行注册。可以确定一个消息和他的处理函数的关联。 


注册了处理函数之后，对应的消息就会被该处理函数处理。 注意到处理函数所在的线程是netty的工作线程，所以如果有阻塞/耗时的逻辑，建议使用一个业务线程池处理。 
