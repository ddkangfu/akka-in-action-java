package com.akkastudy.part3.typedactor;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedActorExtension;
import akka.actor.TypedProps;
import akka.dispatch.Futures;
import akka.dispatch.OnSuccess;
import akka.japi.Option;
import scala.Function1;
import scala.concurrent.Future;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceImpl implements UserService {

    private static Map<String, String> map = new ConcurrentHashMap<>();

    @Override
    public void saveUser(String id, String user) {
        map.put(id, user);
    }

    @Override
    public Future<String> findUserForFuture(String id) {
        return Futures.successful(map.get(id));
    }

    @Override
    public Option<String> findUserForOpt(String id) {
        return Option.some(map.get(id));
    }

    @Override
    public String findUser(String id) {
        return map.get(id);
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        TypedActorExtension typedActorExtension = TypedActor.get(system);

        UserService userService = typedActorExtension.typedActorOf(new TypedProps<UserServiceImpl>(UserService.class, UserServiceImpl.class));
        System.out.println("userService: " + userService);
        userService.saveUser("1", "afei");

        Future<String> fu = userService.findUserForFuture("1");
        fu.onSuccess(new OnSuccess<String>() {
            @Override
            public void onSuccess(String result) throws Throwable, Throwable {
                System.out.println("The future user is : " + result);
            }
        }, system.dispatcher());

        Option<String> opt = userService.findUserForOpt("1");
        System.out.println("The Opt user is: " + opt.getClass());

        String user = userService.findUser("1");
        System.out.println("The user is : " + user);
    }
}
