package com.jdddata.datahub.msghub.service.api;

import com.jdddata.datahub.common.service.consumer.HubPullResult;
import org.apache.rocketmq.client.exception.MQClientException;

/**
 * @ClassName: IConsumer
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 10:18
 * @modified By:
 */
public interface IConsumer {
    void start() throws MQClientException;

    HubPullResult pullMessage(Long offset, Integer max, String topic);

    boolean commitOffset(String topic, Long offset) throws MQClientException;

    void close() throws InterruptedException;

}
