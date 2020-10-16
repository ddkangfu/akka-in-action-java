package com.akkastudy.part2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;


public class BusinessActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception, Exception {
        System.out.println(message);
    }

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(BusinessActor.class).withMailbox("business-mailbox"), "businessActor");
        ref.tell("aaaa", ActorRef.noSender());
    }
}
