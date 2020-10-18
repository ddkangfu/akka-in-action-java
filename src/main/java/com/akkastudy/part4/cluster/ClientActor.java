package com.akkastudy.part4.cluster;

import akka.actor.*;
import akka.cluster.client.*;
import com.typesafe.config.ConfigFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class ServiceListener extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        if (message instanceof ClusterClients) {
            System.out.println("ClusterClients:" + message);
        } else if (message instanceof ClusterClientUp) {
            System.out.println("ClusterClientUp:" + message);
        } else if (message instanceof ClusterClientUnreachable) {
            System.out.println("ClusterClientUnreachable:" + message);
        }
    }
}

class ClientListener extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception, Exception {
        if (message instanceof ContactPoints) {
            System.out.println("ContactPoints:" + message);
        } else if (message instanceof ContactPointAdded) {
            System.out.println("ContactPointAdded:" + message);
        } else if (message instanceof ContactPointRemoved) {
            System.out.println("ContactPointRemoved:" + message);
        }
    }
}

public class ClientActor {
    public static void main(String[] args) {
        Set<ActorPath> initContacts = new HashSet<>(Arrays.asList(
                ActorPaths.fromString("akka.tcp://sys@127.0.0.1:2550/system/receptionist"),
                ActorPaths.fromString("akka.tcp://sys@127.0.0.1:2551/system/receptionist")
        ));
        ActorSystem system = ActorSystem.create("sys1", ConfigFactory.load("clustercli.conf"));
        ActorRef c = system.actorOf(ClusterClient.props(ClusterClientSettings.create(system).withInitialContacts(initContacts)), "client");
        c.tell(new ClusterClient.Send("/user/wordFrontService", "hello", true), ActorRef.noSender());
    }
}
