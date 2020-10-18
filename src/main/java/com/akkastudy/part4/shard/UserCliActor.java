package com.akkastudy.part4.shard;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.sharding.ClusterSharding;
import com.typesafe.config.ConfigFactory;

public class UserCliActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception, Exception {
        ActorRef userRegion = ClusterSharding.get(getContext().system()).shardRegion("userActor");
        if ("buy".equals(message)) {
            Cmd cmd0 = new Cmd(Cmd.BUY, 0);
            cmd0.setItem(new Item(0, "JAVA编程思想", 80));
            userRegion.tell(cmd0, getSelf());

            Cmd cmd1 = new Cmd(Cmd.BUY, 1);
            cmd1.setItem(new Item(1, "C++编程思想", 90));
            userRegion.tell(cmd1, getSelf());

            Cmd cmd2 = new Cmd(Cmd.BUY, 2);
            cmd2.setItem(new Item(2, "AKKA实战", 20));
            userRegion.tell(cmd2, getSelf());
        } else if ("query".equals(message)) {
            userRegion.tell(new Cmd(Cmd.QUERY, 0), getSelf());
            userRegion.tell(new Cmd(Cmd.QUERY, 1), getSelf());
            userRegion.tell(new Cmd(Cmd.QUERY, 2), getSelf());
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("cartcli.conf"));
        ActorRef ref = system.actorOf(Props.create(UserCliActor.class), "cli");
        ref.tell("buy", ActorRef.noSender());
        ref.tell("query", ActorRef.noSender());
    }
}
