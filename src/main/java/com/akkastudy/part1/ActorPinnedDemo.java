package com.akkastudy.part1;

import akka.actor.*;

public class ActorPinnedDemo extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception, Exception {
        System.out.println(getSelf() + "---->" + message + " " + Thread.currentThread().getName());
        Thread.sleep(5000);
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        for (int i = 0; i < 20; i++) {
            ActorRef ref = system.actorOf(Props.create(ActorPinnedDemo.class).withDispatcher("my-forkjoin-dispatcher"), "actorDemo" + i);
            ref.tell("Hello pinned", ActorRef.noSender());
        }
    }
}
