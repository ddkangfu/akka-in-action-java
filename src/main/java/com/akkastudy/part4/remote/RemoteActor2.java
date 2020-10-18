package com.akkastudy.part4.remote;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.typesafe.config.ConfigFactory;

public class RemoteActor2 extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("RemoteActor2: " +message);
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("remote2.conf"));
        system.actorOf(Props.create(RemoteActor2.class), "rmt2");
    }
}
