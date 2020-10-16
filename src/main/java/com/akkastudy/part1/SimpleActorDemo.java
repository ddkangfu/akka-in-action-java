package com.akkastudy.part1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Procedure;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class Emp {
    private float salary;

    private String name;
}

public class SimpleActorDemo extends UntypedActor {
    Procedure<Object> LEVEL1 = new Procedure<Object>() {
        public void apply(Object message) throws Exception, Exception {
            if (message instanceof String) {
                if (message.equals("end")) {
                    getContext().unbecome();
                }
            } else {
                Emp emp = (Emp) message;
                double result = emp.getSalary() * 1.8;
                System.out.println("员工" + emp.getName() + "的奖金为：" + result);
            }
        }
    };

    Procedure<Object> LEVEL2 = new Procedure<Object>() {
        public void apply(Object message) throws Exception, Exception {
            if (message instanceof String) {
                if (message.equals("end")) {
                    getContext().unbecome();
                }
            } else {
                Emp emp = (Emp) message;
                double result = emp.getSalary() * 1.5;
                System.out.println("员工" + emp.getName() + "的奖金为：" + result);
            }
        }
    } ;

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        String level = (String)message;

        if (level.equals("1")) {
            getContext().become(LEVEL1);
        } else if (level.equals("2")) {
            getContext().become(LEVEL2);
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(SimpleActorDemo.class), "simpleActorDemo");
        ref.tell("1", ActorRef.noSender());
        ref.tell(new Emp(10000, "张三"), ActorRef.noSender());
        ref.tell(new Emp(20000, "李四"), ActorRef.noSender());
        ref.tell("end", ActorRef.noSender());
        ref.tell("2", ActorRef.noSender());
        ref.tell(new Emp(10000, "王五"), ActorRef.noSender());
        ref.tell(new Emp(20000, "赵六"), ActorRef.noSender());
    }
}
