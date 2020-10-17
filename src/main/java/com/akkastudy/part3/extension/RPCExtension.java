package com.akkastudy.part3.extension;

import akka.actor.Extension;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RPCExtension implements Extension {
    private String server;
    private int port;

    public void rpcCall(String cmd) {
        System.out.println("call " + cmd + "-->" + server + ":" + port);
    }
}
