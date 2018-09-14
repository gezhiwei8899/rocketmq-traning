package com.jdddata.datahub.msghub.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.codahale.metrics.Meter;
import com.jdddata.datahub.common.service.consumer.ConsumerDataService;
import com.jdddata.datahub.common.service.consumer.HubClientInfo;
import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.ConsumerDataHandler;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName: ConsumerDataServiceImpl
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/11 11:14
 * @modified By:
 */
@Service(timeout = 5000)
public class ConsumerDataServiceImpl implements ConsumerDataService {

    private final Meter requests = Metrics.defaultRegistry().meter("ConsumerDataServiceImpl");

    @Autowired
    private ConsumerDataHandler consumerDataHandler;


    @Override
    public HubPullResult subscribe(String type, String groupName, String uuid, String topic, Long offset, Integer max) {
        requests.mark();
        return consumerDataHandler.consumer(type, groupName, uuid, topic, offset, max);
    }

    @Override
    public boolean commit(String type, String groupName, String uuid, String topic, Long offset) {
        return consumerDataHandler.updateOffset(type, groupName, uuid, topic, offset);
    }

    @Override
    public boolean register(String type, String groupName, String uuid, List<String> topics, HubClientInfo hubClientInfo) {
        try {
            return consumerDataHandler.register(uuid, type, groupName, topics);
        } catch (MQClientException e) {
            return false;
        }
    }
}
