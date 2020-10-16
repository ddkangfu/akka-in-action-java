package com.akkastudy.part2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;

class BroadWorker1 extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(getSelf() + "--->" + message);
    }
}

class BroadWorker2 extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(getSelf() + "--->" + message);
    }
}

public class MasterBroadActor extends UntypedActor {
    private ActorRef router;

    @Override
    public void onReceive(Object message) throws Exception {
        router.tell(message, ActorRef.noSender());
    }

    @Override
    public void preStart() throws Exception {
        getContext().actorOf(Props.create(BroadWorker1.class), "bw1");
        getContext().actorOf(Props.create(BroadWorker2.class), "bw2");
        router = getContext().actorOf(FromConfig.getInstance().props(), "broadRouter");
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef master = system.actorOf(Props.create(MasterBroadActor.class), "masterBroadActor");
        master.tell("helloA", ActorRef.noSender());
        master.tell("helloB", ActorRef.noSender());
    }
}
