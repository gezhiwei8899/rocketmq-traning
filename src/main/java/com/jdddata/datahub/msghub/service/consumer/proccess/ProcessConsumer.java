package com.jdddata.datahub.msghub.service.consumer.proccess;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.IConsumer;
import com.jdddata.datahub.msghub.service.consumer.cache.ConsumerCache;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName: ProcessConsumer
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/18 15:28
 * @modified By:
 */
@Component
public class ProcessConsumer implements CommandLineRunner {

    private static volatile boolean running = true;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(100, r -> new Thread(r, "pullMessageFromMQ"));

    static {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) executorService;
        Metrics.defaultRegistry().register(MetricRegistry.name(ProcessConsumer.class, "queue_size"), (Gauge<Integer>) () -> tpe.getQueue().size());
        Metrics.defaultRegistry().register(MetricRegistry.name(ProcessConsumer.class, "active_size"), (Gauge<Integer>) () -> tpe.getActiveCount());
        Metrics.defaultRegistry().register(MetricRegistry.name(ProcessConsumer.class, "complete_size"), (Gauge<Long>) () -> tpe.getCompletedTaskCount());
        Metrics.defaultRegistry().register(MetricRegistry.name(ProcessConsumer.class, "Task_size"), (Gauge<Long>) () -> tpe.getTaskCount());
    }

    @Override
    public void run(String... args) {
        while (running) {
            Map<String, IConsumer> consumerMap = ConsumerCache.take();
            if (consumerMap.size() == 0) {
                continue;
            }
            for (IConsumer iConsumer : consumerMap.values()) {
                executorService.submit(iConsumer);
            }
        }
    }
}
