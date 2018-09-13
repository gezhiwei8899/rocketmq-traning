package com.jdddata.datahub.msghub.service.producer;

import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.service.api.IProducer;
import com.jdddata.datahub.msghub.service.producer.rocketmq.RocketmqProducer;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: ProducerFactory
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/13 11:01
 * @modified By:
 */
public class ProducerFactory {

    public static IProducer createSend(String mqName) throws RocketMQException {
        IProducer iSender = null;
        if (StringUtils.equalsIgnoreCase(mqName, "kafka")) {
            //TODO
        }
        if (StringUtils.equalsIgnoreCase(mqName, "rocketmq") || null == mqName) {
            iSender = new RocketmqProducer();
        }
        return iSender;
    }
}
