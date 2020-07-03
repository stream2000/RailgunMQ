package cn.stream2000.railgunmq.broker;


import cn.stream2000.railgunmq.broker.server.BrokerParallelServer;
import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest;
import cn.stream2000.railgunmq.core.ProducerMessage.PubMessageRequest.payload_type;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MessageDispatcherTest {

    private final static String clientId = "dsdsdsd-sdsdsdsd-sdsddsd";
    private static BrokerParallelServer brokerParallelServer;

    @BeforeAll
    static void init() {
        brokerParallelServer = new BrokerParallelServer();
        brokerParallelServer.init();
//        brokerParallelServer.start();
    }

    @AfterAll
    static void clean() {
        brokerParallelServer.shutdown();
    }

    @Test
    void mock() {
        for (int i = 0; i < 10; i++) {
            PubMessageRequest req = PubMessageRequest.newBuilder()
                .setChannelId(clientId)
                .setTopic("default")
                .setType(payload_type.Integer)
                .setData(ByteString.copyFromUtf8(Integer.toString(i))).build();
            BusinessTaskExecutor.getBusinessPool()
                .submit(PubMessageTaskFactory.newPubMessageTask(req));

        }
    }
}