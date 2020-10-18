package com.akkastudy.part4.remote;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.typesafe.config.ConfigFactory;

public class RemoteActor1 extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("RemoteActor1: " +message);
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("remote1.conf"));
        system.actorOf(Props.create(RemoteActor1.class), "rmt1");
    }
}
