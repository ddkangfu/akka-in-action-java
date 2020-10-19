package com.akkastudy.part5.websocket;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.ws.TextMessage;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.typesafe.config.ConfigFactory;

import java.util.Arrays;

public class TextMsgTest {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("blank.conf"));
        Materializer mat = ActorMaterializer.create(system);
        TextMessage tm = TextMessage.create("Hello Akka!");
        String strictText = tm.getStrictText();
        System.out.println(strictText);
        System.out.println("========================");
        TextMessage tmSource = TextMessage.create(Source.from(Arrays.asList("上海", "北京", "南京")));
        Source<String, ?> streamText = tmSource.getStreamedText();
        streamText.runWith(Sink.foreach(System.out::println), mat);
    }
}
