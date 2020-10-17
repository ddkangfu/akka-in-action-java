package com.akkastudy.part3.typedactor;

import akka.japi.Option;
import scala.concurrent.Future;

public interface UserService {
    void saveUser(String id, String user);

    Future<String> findUserForFuture(String id);

    Option<String> findUserForOpt(String id);

    String findUser(String id);
}
