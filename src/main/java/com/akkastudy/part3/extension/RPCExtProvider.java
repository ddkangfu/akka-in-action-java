package com.akkastudy.part3.extension;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import com.typesafe.config.Config;

public class RPCExtProvider extends AbstractExtensionId<RPCExtension> {
    private static RPCExtProvider provider = new RPCExtProvider();

    @Override
    public RPCExtension createExtension(ExtendedActorSystem system) {
        Config config = system.settings().config();
        String server = config.getString("akkademo.server");
        int port = config.getInt("akkademo.port");
        return new RPCExtension(server, port);
    }

    public static RPCExtProvider getInstance() {
        return provider;
    }
}
