package com.jdddata.datahub.msghub.service.api;

import com.jdddata.datahub.msghub.common.MsgHubConnectionExcepiton;
import com.jdddata.datahub.common.service.consumer.HubPullResult;
import org.apache.rocketmq.client.exception.MQClientException;

import java.util.List;

/**
 * @ClassName: ConsumerDataHandler
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 10:04
 * @modified By:
 */
public interface ConsumerDataHandler {


    boolean register(String uuid, String type, String groupName, List<String> topics) throws MQClientException;

    HubPullResult consumer(String type, String groupName, String uuid, String topic, Long offset, Integer max) throws MsgHubConnectionExcepiton;

    boolean updateOffset(String type, String groupName, String uuid, String topic, Long offset) throws MsgHubConnectionExcepiton;
}
