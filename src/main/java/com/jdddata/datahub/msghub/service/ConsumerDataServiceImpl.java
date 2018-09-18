package com.jdddata.datahub.msghub.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.codahale.metrics.Meter;
import com.jdddata.datahub.common.service.consumer.ConsumerDataService;
import com.jdddata.datahub.common.service.consumer.HubClientInfo;
import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.common.service.consumer.HubPullStats;
import com.jdddata.datahub.msghub.common.ConsumerRegisterException;
import com.jdddata.datahub.msghub.common.MsgHubConnectionExcepiton;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.ConsumerDataHandler;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static final Logger LOGGER = LoggerFactory.getLogger(ConsumerDataServiceImpl.class);

    private final Meter requestS = Metrics.defaultRegistry().meter("ConsumerDataServiceImpl#subscirbe");
    private final Meter requestC = Metrics.defaultRegistry().meter("ConsumerDataServiceImpl#commit");

    @Autowired
    private ConsumerDataHandler consumerDataHandler;


    @Override
    public HubPullResult subscribe(String type, String groupName, String uuid, String topic, Long offset, Integer max) {
        requestS.mark();
        try {
            return consumerDataHandler.consumer(type, groupName, uuid, topic, offset, max);
        } catch (MsgHubConnectionExcepiton msgHubConnectionExcepiton) {
            return new HubPullResult(HubPullStats.NO_CONNECTION, topic, 0, 0, 0, null);
        }
    }

    @Override
    public boolean commit(String type, String groupName, String uuid, String topic, Long offset) {
        requestC.mark();
        try {
            return consumerDataHandler.updateOffset(type, groupName, uuid, topic, offset);
        } catch (MsgHubConnectionExcepiton msgHubConnectionExcepiton) {
            LOGGER.error("client commit error {} {} {} {}", type, groupName, topic);
            return false;
        }
    }

    @Override
    public boolean register(String type, String groupName, String uuid, List<String> topics, HubClientInfo hubClientInfo) {
        try {
            return consumerDataHandler.register(uuid, type, groupName, topics);
        } catch (MQClientException | ConsumerRegisterException e) {
            LOGGER.error("client register error {} {} {} {}", type, groupName, topics);
            return false;
        }
    }
}
