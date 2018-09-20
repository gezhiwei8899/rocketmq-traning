package com.jdddata.datahub.msghub.service.consumer;

import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.msghub.common.ConsumerRegisterException;
import com.jdddata.datahub.msghub.common.MsgHubConnectionExcepiton;
import com.jdddata.datahub.msghub.common.Utils;
import com.jdddata.datahub.msghub.service.api.ConsumerDataHandler;
import com.jdddata.datahub.msghub.service.api.ConsumerServiceApi;
import com.jdddata.datahub.msghub.service.consumer.cache.Connection;
import com.jdddata.datahub.msghub.service.consumer.cache.ConnectionCache;
import com.jdddata.datahub.msghub.service.consumer.cache.ConsumerCache;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ConsumerDataHandlerImpl
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/18 14:57
 * @modified By:
 */
@Service
public class ConsumerDataHandlerImpl implements ConsumerDataHandler {


    @Autowired
    private ConsumerServiceApi consumerServiceApi;

    @Override
    public synchronized boolean register(String uuid, String type, String groupName, String instanceId, List<String> topics) throws MQClientException, ConsumerRegisterException {
        Connection connection = ConnectionCache.getConnection(uuid);
        if (null == connection) {
            connection = new Connection(uuid, type, groupName, instanceId, topics);
            String key = Utils.consumerKey(type, groupName);
            //如果消费者有缓存存在，那么不用注册IConsumer，直接添加绑定关系
            if (ConsumerCache.exsit(key)) {
                ConsumerCache.cacheConsumerNewConnection(key, connection);
            } else {
                consumerServiceApi.register(type, groupName, topics, connection);
            }
            ConnectionCache.cacheConnection(uuid, type, groupName, instanceId, topics);
        }
        return true;
    }

    @Override
    public boolean updateOffset(String type, String groupName, String uuid, String topic, Long offset) throws MsgHubConnectionExcepiton, MQClientException {
        ConnectionCache.refreshIdleTime(uuid);
        return consumerServiceApi.updateOffset(type, groupName, topic, offset);
    }

    @Override
    public HubPullResult consumer(String type, String groupName, String instanceId, String uuid, String topic, Long offset, Integer max) throws MsgHubConnectionExcepiton {
        ConnectionCache.refreshIdleTime(uuid);
        return consumerServiceApi.pullConsumer(type, groupName, uuid, topic, offset, max);
    }
}
