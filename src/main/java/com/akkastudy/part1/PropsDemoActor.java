package com.akkastudy.part1;

import akka.actor.*;
import akka.japi.Creator;

public class PropsDemoActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception, Exception {

    }

    public static Props createProps() {
        return Props.create(new Creator<Actor>() {
            public PropsDemoActor create() throws Exception, Exception {
                return new PropsDemoActor();
            }
        });
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(PropsDemoActor.createProps(), "propsActor");
    }
}
