package com.akkastudy.part5.http;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.OutgoingConnection;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.CompletionStage;

public class HttpClient {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys", ConfigFactory.load("blank.conf"));
        Materializer mat = ActorMaterializer.create(system);
        Flow<HttpRequest, HttpResponse, CompletionStage<OutgoingConnection>> connFlow = Http.get(system).outgoingConnection(ConnectHttp.toHost("localhost", 8090));
        CompletionStage<HttpResponse> respFuture = Source.single(HttpRequest.create("/shopingcar")).via(connFlow).runWith(Sink.<HttpResponse>head(), mat);
        respFuture.thenAccept(response -> response.entity().getDataBytes().runWith(Sink.foreach(content -> System.out.println(content.utf8String())), mat));
    }
}
