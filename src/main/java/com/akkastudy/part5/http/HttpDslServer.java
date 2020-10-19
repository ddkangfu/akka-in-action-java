package com.akkastudy.part5.http;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

import java.util.concurrent.CompletionStage;

public class HttpDslServer extends AllDirectives {

    public void startApp() {
        ActorSystem system = ActorSystem.create("sys");
        Materializer mat = ActorMaterializer.create(system);
        Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = getRoute().flow(system, mat);
        CompletionStage<ServerBinding> binding = Http.get(system).bindAndHandle(routeFlow, ConnectHttp.toHost("localhost", 8090), mat);
    }

    public Route getRoute() {
        return get(() -> route(
                path("index", () -> complete("欢迎来到首页")),
                path("books", () -> complete("书籍列表")),
                path("book", () -> parameterOptional("bookId", bookId -> {
                    String bid = bookId.orElse("-1");
                    return complete("查询的数据是：" + bid);
                })),
                path("shopingcar", () -> {
                    HttpResponse resp = HttpResponse.create().withEntity(ContentTypes.TEXT_HTML_UTF8, "<font color='red'>购物车列表</font>");
                    return complete(resp);
                })
        ));
    }

    public static void main(String[] args) {
        HttpDslServer server = new HttpDslServer();
        server.startApp();
    }
}
