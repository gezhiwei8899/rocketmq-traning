package com.jdddata.datahub.msghub.service.init;

import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.RocketMqContext;
import com.jdddata.datahub.msghub.service.api.ProducerServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName: AutoRegister
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/13 10:00
 * @modified By:
 */
@Component
public class AutoRegister implements CommandLineRunner {

    @Autowired
    private ProducerServiceApi producerServiceApi;

    @Autowired
    private RocketMqContext rocketMqContext;


    @Override
    public void run(String... args) throws Exception {
        try {
            producerServiceApi.startProducerMsgHub(rocketMqContext);
        } catch (RocketMQException e) {
            e.printStackTrace();
        }
    }
}
