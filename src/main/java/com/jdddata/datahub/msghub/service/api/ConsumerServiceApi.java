package com.jdddata.datahub.msghub.service.api;

import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.msghub.common.ConsumerRegisterException;
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

    boolean updateOffset(String type, String groupName, String topic, Long offset);

    void register(String type, String groupName, List<String> topics) throws ConsumerRegisterException, MQClientException;

    HubPullResult pullConsumer(String type, String groupName, String uuid, String topic, Long offset, Integer max);
}
