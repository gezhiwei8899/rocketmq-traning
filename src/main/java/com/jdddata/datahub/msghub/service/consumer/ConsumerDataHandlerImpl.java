package com.jdddata.datahub.msghub.service.consumer;

import com.jdddata.datahub.msghub.common.MsgHubConnectionExcepiton;
import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.msghub.service.api.ConsumerDataHandler;
import com.jdddata.datahub.msghub.service.api.ConsumerServiceApi;
import com.jdddata.datahub.msghub.service.consumer.connection.Connection;
import com.jdddata.datahub.msghub.service.consumer.connection.ConnectionCache;
import com.jdddata.datahub.msghub.service.consumer.unit.ConsumerCache;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ConsumerDataHandlerImpl
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 10:12
 * @modified By:
 */
@Service
public class ConsumerDataHandlerImpl implements ConsumerDataHandler {

    //当前使劲pull到的一个偏移量
    private static final Map<MessageQueue, Long> MESSAGE_QUEUE_OFFSET = new ConcurrentHashMap<>();

    //当前客户端实际消费的一个偏移量 (groupName_topic,offset)
    private static final Map<String, Long> TOPIC_OFFSET = new ConcurrentHashMap<>();

    @Autowired
    private ConsumerServiceApi consumerServiceApi;


    @Override
    public boolean register(String uuid, String type, String groupName, List<String> topics) throws MQClientException {
        Connection connection = ConnectionCache.getConnection(uuid);
        //null==connection 表示第一次连接，查找是否有缓存的consumers被这个连接所需要，选择需要创建的topic在创建
        if (null == connection) {
            List<String> consumerKeys = Utils.generateConsumerKeys(type, groupName, topics);
            List<String> keys = ConsumerCache.backNotInit(consumerKeys);
            if (consumerServiceApi.register(type, groupName, keys, uuid)) {
                ConnectionCache.putConnectionCache(uuid, consumerKeys);
            }
        } else {
            //TODO
        }

        return true;
    }

    @Override
    public HubPullResult consumer(String type, String groupName, String uuid, String topic, Long offset, Integer max) throws MsgHubConnectionExcepiton {
        ConnectionCache.refreshIdleTime(uuid);
        return consumerServiceApi.pullConsumer(type, groupName, topic, offset, max);
    }

    @Override
    public boolean updateOffset(String type, String groupName, String uuid, String topic, Long offset) throws MsgHubConnectionExcepiton {
        ConnectionCache.refreshIdleTime(uuid);
        return consumerServiceApi.updateOffset(type, groupName, topic, offset);
    }

}
