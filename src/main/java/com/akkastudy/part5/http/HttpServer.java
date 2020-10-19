package com.akkastudy.part5.http;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.IncomingConnection;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.*;
import akka.http.javadsl.model.headers.Location;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.concurrent.CompletionStage;

public class HttpServer {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        Materializer mat = ActorMaterializer.create(system);
        Source<IncomingConnection, CompletionStage<ServerBinding>> bindSource = Http.get(system).bind(ConnectHttp.toHost("localhost", 8089), mat);

        Function<HttpRequest, HttpResponse> processRequest = new Function<HttpRequest, HttpResponse>() {
            HttpResponse resp404 = HttpResponse.create().withStatus(StatusCodes.NOT_FOUND).withEntity("找不到页面.....");

            @Override
            public HttpResponse apply(HttpRequest request) throws Exception, Exception {
                String path = request.getUri().path();
                if (path.equals("/")) {
                    return HttpResponse.create().withEntity(ContentTypes.TEXT_HTML_UTF8, "<font color='red'>欢迎来到首页</font>");
                } else if (path.equals("/items")) {
                    Query query = request.getUri().query();
                    String itemId = query.get("itemId").orElse("not found");
                    return HttpResponse.create().withEntity("查找商品 " + itemId);
                } else if (path.equals("/redirect")) {
                    Location locationHeader = Location.create("http://localhost:8089");
                    return HttpResponse.create().withStatus(StatusCodes.FOUND).addHeader(locationHeader);
                } else {
                    return resp404;
                }
            }
        };

        CompletionStage<ServerBinding> bindFuture = bindSource.to(
                Sink.foreach(conn -> {
                    System.out.println("来自" + conn.remoteAddress() + "的访问！");
                    conn.handleWithSyncHandler(processRequest, mat);
                })
        ).run(mat);
    }
}
