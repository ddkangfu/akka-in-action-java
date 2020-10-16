package com.akkastudy.part1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ActorDemo extends UntypedActor {
    private LoggingAdapter log = Logging.getLogger(this.getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        if (message instanceof String) {
            log.info(message.toString());
        } else {
            unhandled(message);
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(Props.create(ActorDemo.class), "actorDemo");
        actorRef.tell("Hello Akka", ActorRef.noSender());
    }
}
