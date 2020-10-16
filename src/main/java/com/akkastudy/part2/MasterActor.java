package com.akkastudy.part2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;
import akka.routing.RoundRobinGroup;

import java.util.Arrays;
import java.util.List;

class WorkTask extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(getSelf() + "-->" + message + "-->" + getContext().parent());
    }
}

public class MasterActor extends UntypedActor {
    private ActorRef router = null;

    @Override
    public void preStart() throws Exception {
        getContext().actorOf(Props.create(WorkTask.class), "wt1");
        getContext().actorOf(Props.create(WorkTask.class), "wt2");
        getContext().actorOf(Props.create(WorkTask.class), "wt3");
        router = getContext().actorOf(FromConfig.getInstance().props(), "router");
//        List<String> routeePaths = Arrays.asList("/user/masterActor/wt1", "/user/masterActor/wt2", "/user/masterActor/wt3");
//        router = getContext().actorOf(new RoundRobinGroup(routeePaths).props(), "router");
        System.out.println("router:" + router);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        router.tell(message, getSender());
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(MasterActor.class), "masterActor");
        ref.tell("HelloA", ActorRef.noSender());
        ref.tell("HelloB", ActorRef.noSender());
        ref.tell("HelloC", ActorRef.noSender());
    }
}
