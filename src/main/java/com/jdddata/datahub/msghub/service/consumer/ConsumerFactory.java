package com.jdddata.datahub.msghub.service.consumer;

import com.jdddata.datahub.msghub.service.api.IConsumer;
import com.jdddata.datahub.msghub.service.consumer.rocketmq.RocketMqConsumer;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: ConsumerFactory
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 11:38
 * @modified By:
 */
public class ConsumerFactory {
    public static IConsumer createInstance(String s, String s1, String s2) {
        if (null == s || StringUtils.equalsIgnoreCase("rocketmq", s)) {
            return new RocketMqConsumer(s1, s2);
        }
        return null;
    }
}
