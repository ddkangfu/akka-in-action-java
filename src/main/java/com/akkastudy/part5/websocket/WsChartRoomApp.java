package com.akkastudy.part5.websocket;

import akka.NotUsed;
import akka.actor.*;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.IncomingConnection;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.*;
import akka.http.javadsl.model.ws.BinaryMessage;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.model.ws.WebSocket;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.typesafe.config.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

class Register {

}

@AllArgsConstructor
@Getter
class  SendToClient {
    private Message message;
}

@Data
@AllArgsConstructor
class Notice {
    private ActorRef userActor;
}

class Conn {
}

class ChatRoom extends UntypedActor {
    private List<ActorRef> clientActors = new ArrayList<>();
    private Materializer mat;
    public ChatRoom(Materializer mat) {
        this.mat = mat;
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        System.out.println("ChatRoom Stopped ...");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Register) {
            ActorRef userActor = getSender();
            clientActors.add(userActor);
        } else if (message instanceof Message) {
            System.out.println("聊天室列表：" + clientActors.size());
            Message chatMsg = (Message) message;
            if (chatMsg.isText()) {
                TextMessage txtMsg = chatMsg.asTextMessage();
                clientActors.forEach(client -> client.tell(new SendToClient(txtMsg), getSelf()));
            } else {
                BinaryMessage binaryMsg = chatMsg.asBinaryMessage();
                String filePath = "/tmp/httpserver/doc/" + System.currentTimeMillis() + ".jpg";
                CompletionStage<IOResult> comp = binaryMsg.getStreamedData().runWith(FileIO.toPath(Paths.get(filePath)), mat);
                comp.thenAccept(r -> {
                    BinaryMessage binMsg = BinaryMessage.create(FileIO.fromPath(Paths.get(filePath)));
                    clientActors.forEach(client -> client.tell(new SendToClient(binMsg), getSelf()));
                });
            }
        }else if (message instanceof Notice) {
            Notice ter = (Notice) message;
            ActorRef userActor = ter.getUserActor();
            clientActors.remove(userActor);
            getContext().stop(userActor);
        }
    }
}

class User extends UntypedActor {
    private ActorRef clientActor;
    private ActorRef chatRoom;

    public User(ActorRef chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        System.out.println("UserActor Stopped ...");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Conn) {
            this.clientActor = getSender();
            chatRoom.tell(new Register(), getSelf());
        } else if (message instanceof SendToClient) {
            SendToClient sendToClient = (SendToClient)message;
            clientActor.tell(sendToClient.getMessage(), getSelf());
        }
    }
}

public class WsChartRoomApp {
    private ActorSystem system;
    private Materializer mat;

    private ActorRef chatRoom;

    public static void main(String[] args) {
        WsChartRoomApp chat = new WsChartRoomApp();
        chat.startApp();
    }

    private void startApp() {
        system = ActorSystem.create("sys", ConfigFactory.load("blank.conf"));
        mat = ActorMaterializer.create(system);
        Source<IncomingConnection, CompletionStage<ServerBinding>> bindSource = Http.get(system).bind(ConnectHttp.toHost("127.0.0.1", 8090), mat);
        CompletionStage<ServerBinding> bindFuture = bindSource.to(Sink.foreach(conn -> {
            conn.handleWithSyncHandler(processRequest, mat);
        })).run(mat);
        chatRoom = system.actorOf(Props.create(ChatRoom.class, mat), "chatRoom");
    }

    Function<HttpRequest, HttpResponse> processRequest = new Function<HttpRequest, HttpResponse>() {

        @Override
        public HttpResponse apply(HttpRequest request) throws Exception {
            Uri uri = request.getUri();
            if (uri.path().equals("/")) {
                String html = "<!DOCTYPE html><html><head><title>欢迎来到首页</title><meta charset=\"utf-8\">";
                html += "<script type=\"text/javascript\">";
                html += "var client=new WebSocket(\"ws://127.0.0.1:8090/ws_demo\");";
                html += "client.onmessage = function(ey) {";
                html += "var msgContent = document.getElementById(\"msgContent\");";
                html += "var data = ey.data;";
                html += "if (data instanceof Blob) {";
                html += "var fr = new FileReader();";
                html += "fr.readAsDataURL(data);";
                html += "fr.onload = function(evt) {";
                html += "var imgsrc = evt.target.result;";
                html += "msgContent.innerHTML+=(\"<img width='100px' width='140' src='\" + imgsrc + \"'/><br/>\");";
                html += "}} else {";
                html += "msgContent.innerHTML+=(ey.data + \"<br/>\"); }};";
                html += "client.onclose=function(evt) { alert(\"连接关闭\" + evt);};";
                html += "function sendMsg() {";
                html += "var msgInput = document.getElementById(\"msgInput\");";
                html += "var msgValue = msgInput.value;";
                html += "client.send(msgValue);";
                html += "msgInput.value=\"\"; }";
                html += "function uploadMyImg() {";
                html += "var myimg = document.getElementById(\"myimg\");";
                html += "var _file = myimg.files[0];";
                html += "var fr = new FileReader();";
                html += "fr.readAsArrayBuffer(_file);";
                html += "fr.onload=function(evt) {";
                html += "var binaryString = evt.target.result;";
                html += "client.send(binaryString);";
                html += "}} </script></head><body>";
                html += "<div id=\"msgContent\" style=\"border:1px solid;width:300px;min-height:400px\"></div>";
                html += "<input type=\"file\" id=\"myimg\" name=\"myimg\" onchange=\"uploadMyImg()\" /><br/>";
                html += "<input type=\"text\" id=\"msgInput\" /><br/>";
                html += "<input type=\"button\" onclick=\"sendMsg()\" value=\"发送消息\" />";
                html += "</body></html>";

//                String index = FileUtils.readHtml("index.html");
                return HttpResponse.create().withEntity(ContentTypes.TEXT_HTML_UTF8, html);
            } else if (uri.path().equals("/ws_demo")) {
                ActorRef userActor = system.actorOf(Props.create(User.class, chatRoom));
                Sink<Message, NotUsed> inMsg = Sink.actorRef(chatRoom, new Notice(userActor));
                Source<Message, NotUsed> outMsg = Source.<Message>actorRef(100, OverflowStrategy.fail()).mapMaterializedValue(outRef -> {
                    outRef.tell(TextMessage.create("<font color='red'>您已进入聊天室...</font>"), ActorRef.noSender());
                    userActor.tell(new Conn(), outRef);
                    return NotUsed.getInstance();
                });
                Flow<Message, Message, NotUsed> flow = Flow.fromSinkAndSource(inMsg, outMsg);
                return WebSocket.handleWebSocketRequestWith(request, flow);
            } else {
                return HttpResponse.create().withStatus(StatusCodes.NOT_FOUND).withEntity("找不到页面...");
            }
        }
    };
}
