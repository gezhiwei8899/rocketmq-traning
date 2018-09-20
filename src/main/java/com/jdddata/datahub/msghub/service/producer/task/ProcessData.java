package com.jdddata.datahub.msghub.service.producer.task;

import com.jdddata.datahub.common.service.message.HubMessage;
import com.jdddata.datahub.msghub.service.api.ProducerDataHandler;
import com.jdddata.datahub.msghub.service.api.ProducerServiceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static final Logger LOGGER = LoggerFactory.getLogger(ProcessData.class);

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
                        boolean send = producerServiceApi.send(message);
                        if (!send) {
                            LOGGER.error("send message to namespace:{} schema:{} table:{} occured error and this time offset {}", message.getNamespace(), message.getSchema(), message.getTable(), message.getBinlogPosition());
                            //TODO 此处告警或者通知同步器停止工作
                        }
                        LOGGER.debug("send message to namespace:{} schema:{} table:{} and this time offset {}", message.getNamespace(), message.getSchema(), message.getTable(), message.getBinlogPosition());
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("ProcessData error:{}", e);
            } finally {
                producerServiceApi.close();
            }
        }, "ProcessData-#run");
        t.start();
        LOGGER.info("ProcessData started");
    }
}
