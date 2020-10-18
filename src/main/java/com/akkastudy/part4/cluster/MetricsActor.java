package com.akkastudy.part4.cluster;

import akka.actor.UntypedActor;
import akka.cluster.metrics.ClusterMetricsChanged;
import akka.cluster.metrics.ClusterMetricsExtension;
import akka.cluster.metrics.NodeMetrics;
import akka.cluster.metrics.StandardMetrics;

public class MetricsActor extends UntypedActor {
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ClusterMetricsChanged) {
            ClusterMetricsChanged clusterMetrics = (ClusterMetricsChanged) message;
            System.out.println("=======================开始=========================");
            for (NodeMetrics nodeMetrics : clusterMetrics.getNodeMetrics()) {
                String info = "系统信息（" + nodeMetrics.address() + "）\n";
                StandardMetrics.HeapMemory heapMemory = StandardMetrics.extractHeapMemory(nodeMetrics);
                if (heapMemory != null) {
                    double heap = (double)heapMemory.used() / (1024 * 1024);
                    info = info + "Userd Heap: " + heap + "MB\n";
                }
                StandardMetrics.Cpu cpu = StandardMetrics.extractCpu(nodeMetrics);
                if (cpu != null && cpu.systemLoadAverage().isDefined()) {
                    info = info + "Load:" + cpu.systemLoadAverage().get() + "," + cpu.processors();
                }
                System.out.println(info);
            }
            System.out.println("=======================结束=========================");
        }
    }
}
