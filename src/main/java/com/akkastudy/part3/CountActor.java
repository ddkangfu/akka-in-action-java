package com.akkastudy.part3;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;

public class CountActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception, Exception {
        String msg = (String)message;
        getSender().tell(msg.length(), getSelf());
    }

    public static void main(String[] args) {
        String[] words = {"hello", "akka", "future"};
        ActorSystem system = ActorSystem.create("sys");

        Timeout timeout = new Timeout(Duration.create(3, "seconds"));

        List<Future<Integer>> list = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            ActorRef countActor = system.actorOf(Props.create(CountActor.class), "counterActor" + i);
            Future<Integer> future = (Future) Patterns.ask(countActor, words[i], timeout);
            list.add(future);
        }
        ExecutionContext context = system.dispatcher();
        Future<Iterable<Integer>> fs = Futures.sequence(list, context);
        Future<Integer> fv = fs.map(new Mapper<Iterable<Integer>, Integer>() {
            @Override
            public Integer apply(Iterable<Integer> plist) {
                int total = 0;
                for (Integer v : plist) {
                    total += v;
                }
                return total;
            }
        }, system.dispatcher());

        try {
            Integer total = Await.result(fv, timeout.duration());
            System.out.println("结果是：" + total);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
