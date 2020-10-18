package com.akkastudy.part4.shard;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.cluster.Cluster;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardRegion;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UserActor1 extends UntypedPersistentActor {
    private Map<Integer, Item> cart = new HashMap<>();

    Cluster cluster = Cluster.get(getContext().system());

    public UserActor1() {
        context().setReceiveTimeout(Duration.create(60, TimeUnit.SECONDS));
    }

    @Override
    public void preStart() throws Exception, Exception {
        super.preStart();
        System.out.println(cluster.selfAddress() + "." + getSelf() + " start");
    }

    @Override
    public String persistenceId() {
        return "user_" + getSelf().path().name();
    }

    @Override
    public void onReceiveCommand(Object msg) throws Exception, Exception {
        if (msg instanceof Cmd) {
            System.out.println("cmd: " + msg);
            Cmd cmd = (Cmd)msg;
            if (cmd.getAction().equals(Cmd.QUERY)) {
                getSender().tell(cmd.getUserId() + " cart:" + cart, getSelf());
            } else {
                persist(cmd, new Procedure<Cmd>() {
                    @Override
                    public void apply(Cmd param) throws Exception, Exception {
                        handleCmd(param);
                    }
                });
            }
        } else if (msg.equals(ReceiveTimeout.getInstance())) {
            getContext().parent().tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
        } else {
            System.out.println("Other msg: " + msg);
        }
    }

    @Override
    public void onReceiveRecover(Object msg) throws Exception, Exception {
        if (msg instanceof Cmd) {
            Cmd cmd = (Cmd)msg;
            handleCmd(cmd);
        }
    }

    private void handleCmd(Cmd cmd) {
        if (cmd.getAction().equals(Cmd.BUY)) {
            Item item = cmd.getItem();
            cart.put(item.getId(), item);
        } else if (cmd.getAction().equals(Cmd.DEL)) {
            Item item = cmd.getItem();
            cart.remove(item.getId());
        } else {
            System.out.println("Other handlerCmd: " + cmd);
        }
    }

    public static void main(String[] args) {
        ShardRegion.MessageExtractor messageExtractor = new ShardRegion.MessageExtractor() {
            @Override
            public String entityId(Object message) {
                String entityId = null;
                if (message instanceof Cmd) {
                    Cmd cmd = (Cmd) message;
                    entityId = cmd.getUserId() + "";
                }
                return entityId;
            }

            @Override
            public Object entityMessage(Object message) {
                return message;
            }

            @Override
            public String shardId(Object message) {
                String shardId = null;
                int numberOfShards = 10;
                if (message instanceof Cmd) {
                    Cmd cmd = (Cmd)message;
                    shardId = (cmd.getUserId() % numberOfShards) + "";
                }
                return shardId;
            }
        };
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("cart1.conf"));
//        ClusterShardingSettings settings = ClusterShardingSettings.create(system);
//        ClusterSharding.get(system).start("userActor", Props.create(UserActor.class), settings, messageExtractor);
    }
}
