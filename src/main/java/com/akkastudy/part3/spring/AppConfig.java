package com.akkastudy.part3.spring;

import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ActorSystem createActorSystem() {
        ActorSystem system = ActorSystem.create("sys");
        SpringExtProvider.getInstance().get(system).initApplicationContext(applicationContext);
        return system;
    }
}
