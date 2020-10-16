package com.akkastudy.part2;

import akka.actor.*;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.routing.*;
import akka.util.Timeout;
import scala.Function1;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.regex.Pattern;

class FirstWorker1 extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(getSelf() + "--->" + message + " From: " + getSender());
        //Thread.sleep(1000);
        getSender().tell("OK1", getSelf());
    }
}

class FirstWorker2 extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(getSelf() + "--->" + message + " From: " + getSender());
        //Thread.sleep(500);
        getSender().tell("OK2", getSelf());
    }
}

public class MasterFirstActor extends UntypedActor {
    private ActorRef router;

    @Override
    public void onReceive(Object message) throws Exception {
        router.tell(message, ActorRef.noSender());
    }

    @Override
    public void preStart() throws Exception {
        getContext().actorOf(Props.create(FirstWorker1.class), "fw1");
        getContext().actorOf(Props.create(FirstWorker2.class), "fw2");
        router = getContext().actorOf(FromConfig.getInstance().props(), "firstCompRouter");
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef master = system.actorOf(Props.create(MasterFirstActor.class), "masterFirstActor");
        Timeout timeout = new Timeout(Duration.create(10, "seconds"));
        Future<Object> fu = Patterns.ask(master, "helloA", timeout);
        fu.onComplete(new OnComplete<Object>() {
            @Override
            public void onComplete(Throwable failure, Object success) throws Throwable {
                System.out.println("err: " + failure);
                System.out.println("result: " + success);
            }
        }, system.dispatcher());
//        master.tell("Hello", master);
        ActorRef ref = system.actorOf(Props.create(FirstWorker1.class), "fw0");

        master.tell(new AddRoutee(new ActorRefRoutee(ref)), ActorRef.noSender());

        Future<Object> fu1 = Patterns.ask(master, GetRoutees.getInstance(), timeout);
        fu1.onComplete(new OnComplete<Object>() {
            @Override
            public void onComplete(Throwable failure, Object success) throws Throwable {
                Routees rs = (Routees)success;
                List<Routee> routeeList = rs.getRoutees();
                for (Routee r: routeeList) {
                    System.out.println("routee: " + r);
                }
            }
        }, system.dispatcher());

        master.tell(new RemoveRoutee(new ActorRefRoutee(ref)), ActorRef.noSender());
    }
}
