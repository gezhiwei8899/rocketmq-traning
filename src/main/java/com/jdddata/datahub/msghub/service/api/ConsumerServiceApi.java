package com.jdddata.datahub.msghub.service.api;

import com.jdddata.datahub.common.service.consumer.HubPullResult;
import org.apache.rocketmq.client.exception.MQClientException;

import java.util.List;

/**
 * @InterfaceName: ConsumerServiceApi
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 10:11
 * @modified By:
 */
public interface ConsumerServiceApi {

    HubPullResult pullConsumer(String type, String groupName, String topic, Long offset, Integer max);

    boolean updateOffset(String type, String groupName, String topic, Long offset);

    boolean register(String type, String groupName, List<String> keys, String uuid) throws MQClientException;
}
