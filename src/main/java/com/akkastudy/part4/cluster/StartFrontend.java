package com.akkastudy.part4.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.metrics.ClusterMetricsExtension;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class StartFrontend {
    public static void main(String[] args) {
//        String port = args[0];
//        String directory = args[1];
        String port = "2550";
        String directory = "/Users/wuxiaoning/works/test/text/";
        Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.parseString("akka.cluster.roles=[wordFrontend]"))
                .withFallback(ConfigFactory.load("wordcount.conf"));
        ActorSystem system = ActorSystem.create("sys", config);
        ActorRef ref = system.actorOf(Props.create(WordFrontService.class), "wordFrontService");

        ActorRef mRef = system.actorOf(Props.create(MetricsActor.class), "metricsActor");

        ClusterMetricsExtension extension = ClusterMetricsExtension.get(system);
        extension.subscribe(mRef);

        ClusterClientReceptionist.get(system).registerService(ref);

        String result = "";
        while (true) {
            Future<Object> fu = Patterns.ask(ref, "isReady", 1000);
            try {
                result = (String) Await.result(fu, Duration.create(1000, "seconds"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (result.equals("ready")) {
                System.out.println("==================ready==================");
                break;
            }
        }

        List<Article> arts = new ArrayList<>();
        File dir = new File(directory);
        File[] files = dir.listFiles();
        try {
            for (File file : files) {
                StringBuffer contentBf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                while (line != null) {
                    contentBf.append(line);
                    line = reader.readLine();
                }
                reader.close();
                arts.add(new Article(file.getName(), contentBf.toString()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Timeout timeout = new Timeout(Duration.create(3, TimeUnit.SECONDS));
        for (Article art : arts) {
            Patterns.ask(ref, art, timeout).onSuccess(new OnSuccess<Object>() {
                @Override
                public void onSuccess(Object result) throws Throwable {
                    CountResult cr = (CountResult)result;
                    System.out.println("文件 " + cr.getId() + "， 单词数：" + cr.getCount());
                }
            }, system.dispatcher());
        }
    }
}
