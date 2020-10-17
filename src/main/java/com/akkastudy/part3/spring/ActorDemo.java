package com.akkastudy.part3.spring;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedActor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("actorDemo")
@Scope("prototype")
public class ActorDemo extends UntypedActor {
//    @Autowired
//    private EmpService empService;

    @Override
    public void onReceive(Object message) throws Exception, Exception {
        System.out.println("receive: " + message);
//        empService.saveEmp((String)message);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("com.akkastudy.part3.spring");
        ctx.refresh();
        ActorSystem system = ctx.getBean(ActorSystem.class);
        ActorRef ref = system.actorOf(SpringExtProvider.getInstance().get(system).createProps("actorDemo"), "actorDemo");
        ref.tell("hello", ActorRef.noSender());
    }
}
