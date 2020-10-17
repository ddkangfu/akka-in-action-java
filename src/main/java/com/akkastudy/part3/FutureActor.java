package com.akkastudy.part3;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Mapper;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;
import akka.pattern.AskTimeoutException;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.Function1;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class FutureActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        getSender().tell("reply", getSelf());
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(FutureActor.class), "fuActor");

        Timeout timeout = new Timeout(Duration.create(3, "seconds"));
        Future<Object> future = Patterns.ask(ref, "Hello future", timeout);
//        try {
//            String replyMsg = (String) Await.result(future, timeout.duration());
//            System.out.println(replyMsg);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        future.onSuccess(new OnSuccess<Object>() {
            @Override
            public void onSuccess(Object result) throws Throwable, Throwable {
                System.out.println("receive: " + result);
            }
        }, system.dispatcher());

        future.onFailure(new OnFailure() {
            @Override
            public void onFailure(Throwable failure) throws Throwable, Throwable {
                if (failure instanceof AskTimeoutException) {
                    System.out.println("超时异常");
                } else {
                    System.out.println("其它异常" + failure);
                }
            }
        }, system.dispatcher());

        Future<String> f2 = future.map(new Mapper<Object, String>() {
            @Override
            public String apply(Object parameter) {
                return ((String)parameter).toUpperCase();
            }
        }, system.dispatcher());
//        f2.onSuccess();
    }
}
