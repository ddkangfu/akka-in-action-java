package com.akkastudy.part1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Procedure;

public class BecomeActor extends UntypedActor {
    Procedure<Object> proce = new Procedure<Object>() {
        public void apply(Object param) throws Exception, Exception {
            System.out.println("become:" + param);
        }
    };

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        System.out.println("接收消息：" + message);
        getContext().become(proce);
        System.out.println("----------------------");
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(BecomeActor.class), "becomeActor");
        ref.tell("hello", ActorRef.noSender());
        ref.tell("hi", ActorRef.noSender());
        ref.tell("hi", ActorRef.noSender());
        ref.tell("hi", ActorRef.noSender());
    }
}
