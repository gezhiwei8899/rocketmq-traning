package com.jdddata.datahub.msghub.service.consumer;

import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.msghub.config.RocketMqContext;
import com.jdddata.datahub.msghub.service.api.ConsumerServiceApi;
import com.jdddata.datahub.msghub.service.api.IConsumer;
import com.jdddata.datahub.msghub.service.consumer.unit.ConsumerCache;
import com.jdddata.datahub.msghub.service.consumer.unit.ConsumerUnit;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName: ConsumerBase
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 10:04
 * @modified By:
 */
@Service
public class ConsumerBase implements ConsumerServiceApi {


    private static final ExecutorService executorService = Executors.newFixedThreadPool(10, r -> new Thread(r, "pullMessageFromMQ"));

    @Autowired
    private RocketMqContext rocketMqContext;

    static {
    }


    @Override
    public HubPullResult pullConsumer(String type, String groupName, String topic, Long offset, Integer max) {
        String key = Utils.generateConsumerKey(type, groupName, topic);
        ConsumerUnit consumerUnit = ConsumerCache.getConsumerUnit(key);
        IConsumer iConsumer = consumerUnit.getiConsumer();
        return iConsumer.pullMessage(offset, max);
    }

    @Override
    public boolean updateOffset(String type, String groupName, String topic, Long offset) {
        String key = Utils.generateConsumerKey(type, groupName, topic);
        ConsumerUnit consumerUnit = ConsumerCache.getConsumerUnit(key);
        IConsumer iConsumer = consumerUnit.getiConsumer();
        if (null == iConsumer) {
            return false;
        }
        return iConsumer.updateOffset(type, groupName, topic, offset);
    }


    @Override
    public boolean register(String type, String groupName, List<String> keys, String uuid) throws MQClientException {
        for (String key : keys) {
            String topic = key.replace(type + "_" + groupName + "_", "");

            ConsumerUnit consumerUnit = ConsumerCache.getConsumerUnit(key);
            if (null != consumerUnit) {
                continue;
            }
            IConsumer iConsumer = ConsumerFactory.createInstance(type, groupName, topic, rocketMqContext);
            iConsumer.start();
            //TODO 阻塞队列，考虑循环Connection队列执行
            executorService.submit(iConsumer);
            iConsumer.setRunninged(true);
            ConsumerCache.putConsumerToCache(key, iConsumer, uuid);

        }
        return true;
    }
}
