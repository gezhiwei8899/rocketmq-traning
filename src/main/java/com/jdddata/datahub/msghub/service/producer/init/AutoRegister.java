package com.jdddata.datahub.msghub.service.producer.init;

import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.MsgHubContext;
import com.jdddata.datahub.msghub.service.api.ProducerServiceApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static final Logger LOGGER = LoggerFactory.getLogger(AutoRegister.class);

    @Autowired
    private ProducerServiceApi producerServiceApi;

    @Autowired
    private MsgHubContext msgHubContext;


    @Override
    public void run(String... args) throws Exception {
        try {
            producerServiceApi.initProducer(msgHubContext);
        } catch (RocketMQException e) {
            LOGGER.error("init producer error please check");
        }
    }
}
