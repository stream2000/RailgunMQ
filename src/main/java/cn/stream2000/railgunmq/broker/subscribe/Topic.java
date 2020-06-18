package cn.stream2000.railgunmq.broker.subscribe;

import java.util.ArrayList;

public class Topic {

    // use array to organize subscriptions so that we can access them by index
    // we don't provide a fanout queue in this iteration.
    private final ArrayList<Subscription> subscriptions = new ArrayList<>();
    private int consumedIndex = 0;

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
        if(subscriptions.size() == 1){
            // TODO start fake client of this topic to send offline and un-ack messages
        }
        return true;
    }
}
