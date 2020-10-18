package com.akkastudy.part4.cluster;

import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SingletonActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception, Exception {
        if (message instanceof ClusterEvent.MemberUp) {
            ClusterEvent.MemberUp mu = (ClusterEvent.MemberUp)message;
            Member m = mu.member();
            System.out.println(m + " is up");
        } else {
            unhandled(message);
        }
    }
}
