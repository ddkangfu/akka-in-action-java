package com.akkastudy.part3.spring;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;

public class SpringExtProvider extends AbstractExtensionId<SpringExt> {
    private static SpringExtProvider provider = new SpringExtProvider();

    @Override
    public SpringExt createExtension(ExtendedActorSystem system) {
        return new SpringExt();
    }

    public static SpringExtProvider getInstance() {
        return provider;
    }
}
