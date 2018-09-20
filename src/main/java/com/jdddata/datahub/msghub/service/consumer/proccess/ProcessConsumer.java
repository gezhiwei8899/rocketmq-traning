package com.jdddata.datahub.msghub.service.consumer.proccess;

import com.jdddata.datahub.msghub.service.api.IConsumer;
import com.jdddata.datahub.msghub.service.consumer.cache.ConsumerCache;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3, r -> new Thread(r, "pullMessageFromMQ"));

    @Override
    public void run(String... args) {
        Thread t = new Thread(() -> {
            while (running) {
                try {
                    IConsumer iConsumer = ConsumerCache.take();
                    if (null == iConsumer) {
                        continue;
                    }
                    executorService.submit(iConsumer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "ProcessConsumer#Take");
        t.start();

        Thread t2 = new Thread(() -> {
            while (running) {
                ConsumerCache.put();
            }
        }, "ProcessConsumer#put");
        t2.start();
    }
}
