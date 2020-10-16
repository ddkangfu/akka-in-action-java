package com.akkastudy.part2;

import akka.actor.*;
import akka.japi.Function;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;
import scala.concurrent.duration.Duration;

class TaskActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(getSelf() + "-->" + message + "-->" + getContext().parent());
    }
}

public class MasterRouterActor extends UntypedActor {
    ActorRef router = null;

    @Override
    public void preStart() throws Exception {
        SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create("1 minute"), new Function<Throwable, SupervisorStrategy.Directive>() {
            public SupervisorStrategy.Directive apply(Throwable param) throws Exception {
                return null;
            }
        });

//        router = getContext().actorOf(new RoundRobinPool(3).withSupervisorStrategy(strategy).props(Props.create(TaskActor.class)), "taskActor");
        router = getContext().actorOf(FromConfig.getInstance().withSupervisorStrategy(strategy).props(Props.create(TaskActor.class)), "taskActor");

        System.out.println("router:" + router);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        router.tell(message, getSender());
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(MasterRouterActor.class), "masterRouterActor");
        ref.tell("HelloA", ActorRef.noSender());
        ref.tell("HelloB", ActorRef.noSender());
        ref.tell("HelloC", ActorRef.noSender());
    }
}
