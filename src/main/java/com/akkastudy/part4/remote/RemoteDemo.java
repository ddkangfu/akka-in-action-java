package com.akkastudy.part4.remote;

import akka.actor.*;


public class RemoteDemo extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("Receive msg: "  + message);
        getSender().tell("HaHahaha", getSelf());
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");

        ActorRef ref = system.actorOf(Props.create(RemoteDemo.class), "remoteActor");
    }
}
