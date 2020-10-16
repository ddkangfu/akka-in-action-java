package com.akkastudy.part1;

import akka.actor.*;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.util.Timeout;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;


class TargetActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("target receive:" + message);
    }
}

public class SelectionTestActor extends UntypedActor {
    private ActorRef target = null;

    {
        target = getContext().actorOf(Props.create(TargetActor.class), "targetActor");
    }

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        if (message instanceof String) {
            if ("find".equals(message)) {
                System.out.println("Get find message");
                ActorSelection as = getContext().actorSelection("targetActor");
                as.tell(new Identify("A001"), getSelf());
            }
        } else if (message instanceof ActorIdentity) {
            System.out.println("Get find Identity");
            ActorIdentity ai = (ActorIdentity) message;
            if (ai.correlationId().equals("A001")) {
                ActorRef ref = ai.getRef();
                if (ref != null) {
                    System.out.println("ActorIdentity: " + ai.correlationId() + " " + ref);
                    ref.tell("hello target", getSelf());
                }
            }
        } else {
            System.out.println("unhandled");
            unhandled(message);
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(SelectionTestActor.class), "select");
        ref.tell("find", ActorRef.noSender());

        ActorSelection as = system.actorSelection("/user/select/targetActor");
        Timeout timeout = new Timeout(Duration.create(2, "seconds"));
        Future<ActorRef> fu = as.resolveOne(timeout);
        fu.onSuccess(new OnSuccess<ActorRef>() {
            @Override
            public void onSuccess(ActorRef ref) throws Throwable, Throwable {
                System.out.println("查找到Actor:" + ref);
            }
        }, system.dispatcher());
        fu.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable ex) throws Throwable, Throwable {
                System.out.println("没找到Actor:" + ex.getMessage());
            }
        }, system.dispatcher());
    }
}
