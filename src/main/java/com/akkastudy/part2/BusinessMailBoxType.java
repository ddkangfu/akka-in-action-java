package com.akkastudy.part2;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.Envelope;
import akka.dispatch.MailboxType;
import akka.dispatch.MessageQueue;
import akka.dispatch.ProducesMessageQueue;
import com.typesafe.config.Config;
import scala.Option;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class BusinessMsgQueue implements MessageQueue {
    private Queue<Envelope> queue = new ConcurrentLinkedQueue<Envelope>();

    public void enqueue(ActorRef receiver, Envelope el) {
        queue.offer(el);
    }

    public Envelope dequeue() {
        return queue.poll();
    }

    public int numberOfMessages() {
        return queue.size();
    }

    public boolean hasMessages() {
        return !queue.isEmpty();
    }

    public void cleanUp(ActorRef owner, MessageQueue deadLetters) {
        for (Envelope el: queue) {
            deadLetters.enqueue(owner, el);
        }
    }
}

public class BusinessMailBoxType implements MailboxType, ProducesMessageQueue<BusinessMsgQueue> {
    public BusinessMailBoxType(ActorSystem.Settings settings, Config config) {

    }

    public MessageQueue create(Option<ActorRef> owner, Option<ActorSystem> system) {
        return new BusinessMsgQueue();
    }
}

