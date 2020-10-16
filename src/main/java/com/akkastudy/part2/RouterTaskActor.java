package com.akkastudy.part2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;
import java.util.List;

class RouteeActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(getSelf() + "--->" + message);
    }
}

public class RouterTaskActor extends UntypedActor {
    private Router router;

    @Override
    public void preStart() throws Exception {
        List<Routee> listRoutee = new ArrayList<Routee>();
        for (int i = 0; i < 2; i++) {
            ActorRef ref = getContext().actorOf(Props.create(RouteeActor.class), "routeeActor" + i);
            listRoutee.add(new ActorRefRoutee(ref));
        }
        router = new Router(new RoundRobinRoutingLogic(), listRoutee);
    }

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        router.route(message, getSender());
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(RouterTaskActor.class), "routerTaskActor");
        ref.tell("HelloA", ActorRef.noSender());
        ref.tell("HelloB", ActorRef.noSender());
        ref.tell("HelloC", ActorRef.noSender());
    }
}
