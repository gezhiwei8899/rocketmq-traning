package com.jdddata.datahub.msghub.message;

import com.jdddata.datahub.common.service.Message;
import com.jdddata.datahub.msghub.producer.ProducerBase;
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
public class ProcessData implements CommandLineRunner, BaseEumu {

    private volatile boolean running = true;

    @Autowired
    private IReceive iReceive;


    @Override
    public void run(String... args) {
        Thread t = new Thread(() -> {
            try {
                while (running) {
                    Message message = iReceive.take();
                    if (null != message) {
                        boolean send = ProducerBase.INSTANCE.send(message.getNamespace(), message.getSchema(), message);
                        if (!send) {

                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                ProducerBase.close();
            }
        });
        t.start();

    }
}
