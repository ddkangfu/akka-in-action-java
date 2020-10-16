package com.akkastudy.part2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.ControlMessage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class ControlMsg implements ControlMessage {
    private final String status;

    @Override
    public String toString() {
        return status;
    }
}

public class ControlAwareActor extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println(message);
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(ControlAwareActor.class).withMailbox("control-aware-mailbox"), "controlAware");
        Object[] messages = {"Java", "c#", new ControlMsg("ServerPage"), "PHP"};
        for (Object msg:messages) {
            ref.tell(msg, ActorRef.noSender());
        }
    }
}
