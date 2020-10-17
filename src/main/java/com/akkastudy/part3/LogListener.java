package com.akkastudy.part3;

import akka.actor.UntypedActor;
import akka.event.Logging;

public class LogListener extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Logging.InitializeLogger) {
            System.out.println("init: " + message);
            getSender().tell(Logging.loggerInitialized(), getSelf());
        } else if (message instanceof Logging.Error) {
            System.out.println("error: " + message);
        } else if (message instanceof Logging.Warning) {
            System.out.println("warn: " + message);
        } else if (message instanceof Logging.Info) {
            System.out.println("info: " + message);
        } else if (message instanceof Logging.Debug) {
            System.out.println("debug: " + message);
        } else {
            System.out.println("unhandled:" +message);
        }
    }
}
