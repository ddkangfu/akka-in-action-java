package com.akkastudy.part1;

import akka.actor.*;
import akka.japi.Function;
import scala.Option;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.sql.SQLException;

public class SupervisorActor extends UntypedActor {
    private SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create("1 minute"), new Function<Throwable, SupervisorStrategy.Directive>() {
        public SupervisorStrategy.Directive apply(Throwable t) throws Exception, Exception {
            if (t instanceof IOException) {
                System.out.println("========== IOException =========");
                return SupervisorStrategy.resume();
            } else if (t instanceof IndexOutOfBoundsException) {
                System.out.println("========== IndexOutOfBoundsException =========");
                return SupervisorStrategy.restart();
            } else if (t instanceof SQLException) {
                System.out.println("========== SQLException =========");
                return SupervisorStrategy.stop();
            } else {
                System.out.println("========== escalate =========");
                return SupervisorStrategy.escalate();
            }
        }
    });

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        if (message instanceof Terminated) {
            Terminated ter = (Terminated) message;
            System.out.println(ter.getActor() + "已经终止");
        } else {
            System.out.println("stateCount=" + message);
        }
    }

    @Override
    public void preStart() throws Exception {
        ActorRef workerActor2 = getContext().actorOf(Props.create(WorkerActor2.class), "workerActor2");
        getContext().watch(workerActor2);
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("sys");
        ActorRef workerRef = actorSystem.actorOf(Props.create(SupervisorActor.class), "supervsior");
//        workerRef.tell(new IOException(), );
    }
}

class WorkerActor2 extends UntypedActor {
    private int stateCount = 1;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        System.out.println("worker actor preStart");
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        System.out.println("worker actor postStop");
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception, Exception {
        System.out.println("worker actor preRestart begin " + this.stateCount);
        super.preRestart(reason, message);
        System.out.println("work actor preRestart end " + this.stateCount);
    }

    @Override
    public void postRestart(Throwable reason) throws Exception, Exception {
        System.out.println("worker actor postRestart begin " + this.stateCount);
        super.postRestart(reason);
        System.out.println("worker actor postRestart end " + this.stateCount);
    }

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        this.stateCount++;
        if (message instanceof Exception) {
            throw (Exception)message;
        } else if ("getValue".equals(message)) {
            getSender().tell(stateCount, getSelf());
        } else {
            unhandled(message);
        }
    }
}
