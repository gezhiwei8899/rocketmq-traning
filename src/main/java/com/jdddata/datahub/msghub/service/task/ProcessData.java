package com.jdddata.datahub.msghub.service.task;

import com.jdddata.datahub.common.service.message.HubMessage;
import com.jdddata.datahub.msghub.service.api.ProducerDataHandler;
import com.jdddata.datahub.msghub.service.api.ProducerServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName: ProcessData
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 10:33
 * @modified By:
 */
@Component
public class ProcessData implements CommandLineRunner {

    private volatile boolean running = true;

    @Autowired
    private ProducerDataHandler producerDataHandler;

    @Autowired
    private ProducerServiceApi producerServiceApi;


    @Override
    public void run(String... args) {
        Thread t = new Thread(() -> {
            try {
                while (running) {
                    HubMessage message = producerDataHandler.take();
                    if (null != message) {
                        boolean send = producerServiceApi.send(message.getNamespace(), message.getSchema(), message);
                        if (!send) {

                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                producerServiceApi.close();
            }
        },"ProducerDataHandler-take-thread");
        t.start();

    }
}
