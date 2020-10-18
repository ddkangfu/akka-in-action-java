package com.akkastudy.part4.shard;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Procedure;
import akka.persistence.RecoveryCompleted;
import akka.persistence.SaveSnapshotSuccess;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentActor;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
class Action implements Serializable {
    private String cmd;
    private String data;

    @Override
    public String toString() {
        return this.cmd + "------>" + this.data;
    }
}

public class UserService extends UntypedPersistentActor {
    private List<Action> states = new ArrayList<>();

    @Override
    public String persistenceId() {
        return "userservice-1";
    }

    @Override
    public void onReceiveCommand(Object msg) throws Exception {
        if (msg instanceof Action) {
            Action action = (Action) msg;
            if (action.getCmd().equals("save")) {
                persist(action, new Procedure<Action>() {
                    @Override
                    public void apply(Action act) throws Exception {
                        states.add(act);
                    }
                });
            } else if (action.getCmd().equals("saveAll")) {
                saveSnapshot(states);
            } else if (action.getCmd().equals("get")) {
                System.out.println("state: " + states);
            }
        } else if (msg instanceof SaveSnapshotSuccess) {
            SaveSnapshotSuccess saveSnapshotSuccess = (SaveSnapshotSuccess)msg;
            System.out.println("Save snap success: " + saveSnapshotSuccess.metadata());
        } else {
            System.out.println("other message " + msg);
        }
    }

    @Override
    public void onReceiveRecover(Object msg) throws Exception {
        if (msg instanceof Action) {
            Action evt = (Action) msg;
            states.add(evt);
        } else if (msg instanceof SnapshotOffer) {
            SnapshotOffer snapshotOffer = (SnapshotOffer) msg;
            states = (List<Action>) snapshotOffer.snapshot();
            System.out.println("recover: " + states);
        } else if (msg instanceof RecoveryCompleted) {
            System.out.println("replay has been finished");
        }
        System.out.println("onReceiveRecover: " + msg + ", " + msg.getClass());
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("blank.conf"));
        ActorRef ref = system.actorOf(Props.create(UserService.class), "userService");
        ref.tell(new Action("save", "00000"), ActorRef.noSender());
        ref.tell(new Action("get", "9999"), ActorRef.noSender());
        ref.tell(new Action("save", "00001"), ActorRef.noSender());
        ref.tell(new Action("get", "77777"), ActorRef.noSender());
        ref.tell(new Action("saveAll", "77777"), ActorRef.noSender());
    }
}
