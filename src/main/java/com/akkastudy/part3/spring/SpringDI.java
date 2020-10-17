package com.akkastudy.part3.spring;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class SpringDI implements IndirectActorProducer {
    private ApplicationContext applicationContext;

    private String beanName;

    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(beanName);
    }

    @Override
    public Actor produce() {
        return (Actor) applicationContext.getBean(beanName);
    }
}
