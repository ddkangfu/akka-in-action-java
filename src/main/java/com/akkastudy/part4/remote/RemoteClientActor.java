package com.akkastudy.part4.remote;

import akka.actor.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RemoteClientActor extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof String) {
            if ("talk".equals(message)) {
                ActorSelection selection = getContext().actorSelection("akka.tcp://sys@127.0.0.1:2552/user/remoteActor");
                selection.tell(new Identify("666"), getSelf());
            } else {
                System.out.println(message);
            }
        } else if (message instanceof ActorIdentity) {
            ActorIdentity ai = (ActorIdentity) message;
            ActorRef actorRef = ai.getRef();
            if (actorRef != null) {
                actorRef.tell("Hello remoteActor", getSelf());
            }
        } else {
            unhandled(message);
        }
    }

    public static void main(String[] args) {
//        Config config = ConfigFactory.load();
        ActorSystem system = ActorSystem.create("sysCli", ConfigFactory.load("remote-cli.conf") );
        ActorRef actorRef = system.actorOf(Props.create(RemoteClientActor.class), "cliActor");
        actorRef.tell("talk", ActorRef.noSender());
    }
}
