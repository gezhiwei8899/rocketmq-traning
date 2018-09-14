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
public interface IConsumer extends Runnable {

    boolean isRunninged();

    void setRunninged(boolean b);

    HubPullResult pullMessage() throws InterruptedException;

    HubPullResult pullMessage(Long offset, Integer max);

    boolean updateOffset(String type, String groupName, String topic, Long offset);

    void start() throws MQClientException;

    void clear();
}
