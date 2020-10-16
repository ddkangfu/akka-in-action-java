package com.akkastudy.part2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;



public class MsgPriorityActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception, Exception {
        System.out.println(message);
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("system");
        ActorRef ref = system.actorOf(Props.create(MsgPriorityActor.class).withMailbox("msgprio-mailbox"), "priorityActor");
        Object[] messages = {"王五", "李四", "张三", "小二"};
        for (Object msg : messages) {
            ref.tell(msg, ActorRef.noSender());
        }
    }
}
