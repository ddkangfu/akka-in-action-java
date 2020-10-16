package com.akkastudy.part1;

import akka.actor.*;
import akka.japi.Function;
import akka.pattern.CircuitBreaker;
import scala.concurrent.duration.Duration;

import java.util.concurrent.Callable;

public class CircuitBreakerActor extends UntypedActor {

    private ActorRef workerChild;

    private static SupervisorStrategy strategy = new OneForOneStrategy(20, Duration.create("1 minute"), new Function<Throwable, SupervisorStrategy.Directive>() {
        public SupervisorStrategy.Directive apply(Throwable param) throws Exception, Exception {
            return SupervisorStrategy.resume();
        }
    });

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void preStart() throws Exception, Exception {
        super.preStart();
        workerChild = getContext().actorOf(Props.create(WorkerActor3.class), "workerActor3");
    }

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        workerChild.tell(message, getSender());
    }
}

class WorkerActor3 extends UntypedActor {
    private CircuitBreaker breaker;

    @Override
    public void preStart() throws Exception, Exception {
        super.preStart();
        this.breaker = new CircuitBreaker(getContext().dispatcher(), getContext().system().scheduler(), 5, Duration.create(2, "s"), Duration.create(1, "min")).onOpen(new Runnable() {
            public void run() {
                //System.out.println(DateUtils.getCurrentDateStr() + "-->Actor C工rcuitBreaker 开启");
                System.out.println("-->Actor C工rcuitBreaker 开启");
            }
        }).onHalfOpen(new Runnable() {
            public void run() {
                //System.out.println(DateUtils.getCurrentDateStr + "-->Actor Ci rcuitBreaker 半开启");
                System.out.println("-->Actor Ci rcuitBreaker 半开启");
            }
        }).onClose(new Runnable() {
            public void run() {
                //System.out.println(DateUtils.getCurrentDateStr + "-->Actor Ci rcuitBreaker 关闭");
                System.out.println("-->Actor Ci rcuitBreaker 关闭");
            }
        });
    }

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        if (message instanceof String) {
            final String msg = (String) message;
            if (msg.startsWith("sync")) {
                getSender().tell(breaker.callWithSyncCircuitBreaker(new Callable<Object>() {

                    public Object call() throws Exception {
                        System.out.println("msg:" + msg);
                        Thread.sleep(3000);
                        return msg;
                    }
                }), getSelf());
            }
        }
    }
}
