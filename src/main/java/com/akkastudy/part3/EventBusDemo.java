package com.akkastudy.part3;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.japi.LookupEventBus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class Event {
    private String type;

    private String message;
}

class EventSubActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception, Exception {
        System.out.println(message);
    }
}

public class EventBusDemo extends LookupEventBus<Event, ActorRef, String> {
    @Override
    public String classify(Event event) {
        return event.getType();
    }

    @Override
    public int compareSubscribers(ActorRef a, ActorRef b) {
        return a.compareTo(b);
    }

    @Override
    public void publish(Event event, ActorRef ref) {
        ref.tell(event.getMessage(), ActorRef.noSender());
    }

    @Override
    public int mapSize() {
        return 8;
    }

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("sys");
        ActorRef eventSubActor = actorSystem.actorOf(Props.create(EventSubActor.class), "eventSubActor");

        EventBusDemo bus = new EventBusDemo();
        bus.subscribe(eventSubActor, "info");
        bus.subscribe(eventSubActor, "warn");

        bus.publish(new Event("info", "Hello EventBus"));

        bus.publish(new Event("warn", "Oh No"));

        bus.unsubscribe(eventSubActor, "warn");

        bus.publish(new Event("warn", "Oh No Again"));
    }
}
