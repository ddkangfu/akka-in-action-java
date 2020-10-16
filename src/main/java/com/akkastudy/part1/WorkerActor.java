package com.akkastudy.part1;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

class WatchActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(this.getContext().system(), this);
    ActorRef child = null;

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        if (message instanceof String) {
            if (message.equals("stopChild")) {
                getContext().stop(child);
            }
        } else if (message instanceof Terminated) {
            Terminated t = (Terminated) message;
            log.info("监控到" + t.getActor() + "停止了");
        } else {
             unhandled(message);
        }
    }

    @Override
    public void preStart() throws Exception {
        child = getContext().actorOf(Props.create(WorkerActor.class), "workerActor");
        getContext().watch(child);
    }

    @Override
    public void postStop() throws Exception {
        log.info("WatchActor postStop");
    }
}

public class WorkerActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(this.getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {
        log.info("收到消息：" + message);
    }

    @Override
    public void postStop() throws Exception {
        log.info("Worker postStop");
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(WatchActor.class), "watchActor");
//        system.stop(ref);
//        ref.tell(PoisonPill.getInstance(), ActorRef.noSender());
//        ref.tell(Kill.getInstance(), ActorRef.noSender());
        ref.tell("stopChild", ActorRef.noSender());
    }
}
