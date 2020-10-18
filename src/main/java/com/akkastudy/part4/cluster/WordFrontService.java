package com.akkastudy.part4.cluster;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

import java.util.ArrayList;
import java.util.List;

public class WordFrontService extends UntypedActor {
    private List<ActorRef> wordCountServices = new ArrayList<>();

    private int jobCounter = 0;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Article) {
            jobCounter++;
            Article art = (Article)message;
            int serviceNodeIndex = jobCounter % wordCountServices.size();
            System.out.println("选择节点：" + serviceNodeIndex);
            wordCountServices.get(serviceNodeIndex).forward(art, getContext());
        } else if (message instanceof String) {
            String cmd = (String)message;
            if (cmd.equals("serviceIsOK")) {
                ActorRef backendSender = getSender();
                System.out.println(backendSender + " 可用");
                wordCountServices.add(backendSender);
                getContext().watch(backendSender);
            } else if (cmd.equals("isReady")) {
                if (!wordCountServices.isEmpty()) {
                    getSender().tell("ready", getSelf());
                } else {
                    getSender().tell("notReady", getSelf());
                }
            }
        } else if (message instanceof Terminated) {
            Terminated ter = (Terminated)message;
            System.out.println("移除了 " + ter.getActor());
            wordCountServices.remove(ter.getActor());
        } else {
            unhandled(message);
        }
    }
}
