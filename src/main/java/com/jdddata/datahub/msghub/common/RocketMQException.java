package com.jdddata.datahub.msghub.common;

import org.apache.rocketmq.client.exception.MQClientException;

/**
 * @ClassName: RocketMQException
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 15:26
 * @modified By:
 */
public class RocketMQException extends Throwable {
    public RocketMQException(String msg) {
    }

    public RocketMQException(String msg, MQClientException e) {

    }
}
