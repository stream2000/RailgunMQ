package cn.stream2000.railgunmq.broker.subscribe;

import cn.stream2000.railgunmq.store.OfflineMessageStore;
import java.util.ArrayList;

public class Topic {

    // use array to organize subscriptions so that we can access them by index
    // we don't provide a fanout queue in this iteration.
    private final ArrayList<Subscription> subscriptions = new ArrayList<>();
    private OfflineFakeClient offlineFakeClient;
    private final String topicName;
    private int consumedIndex = 0;
    public Topic(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }

    synchronized public Subscription getNextSubscription() {
        if (subscriptions.size() == 0) {
            return null;
        } else {
            var sub = subscriptions.get(subscriptions.size() % consumedIndex);
            consumedIndex++;
            return sub;
        }
    }

    synchronized public boolean removeSubscription(String clientId) {
        for (Subscription c : subscriptions) {
            if (c.getClientId().equals(clientId)) {
                subscriptions.remove(c);
                if(subscriptions.size() == 0){
                    if(offlineFakeClient != null){
                        offlineFakeClient.stop();
                    }
                }
                return true;
            }
        }
        return false;
    }

    // when a new client try to
    synchronized public boolean addSubscription(Subscription sub) {
        for (Subscription c : subscriptions) {
            if (c.getClientId().equals(sub.getClientId())) {
                return false;
            }
        }
        subscriptions.add(sub);
        if (subscriptions.size() == 1) {
            // TODO start a fake client of this topic to send offline and un-ack messages
            offlineFakeClient = OfflineFakeClientFactory.newOfflineFakeClient(topicName);
            new Thread(offlineFakeClient).start();
        }
        return true;
    }

    synchronized public boolean isActive() {
        return !subscriptions.isEmpty();
    }

    synchronized public boolean checkAndStore(OfflineMessageStore offlineMessageStore,
        String msgId) {
        if (isActive()) {
            return false;
        } else {
            offlineMessageStore.addMessage(topicName, msgId);
            return true;
        }
    }
}
