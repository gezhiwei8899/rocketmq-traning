package com.jdddata.datahub.msghub.service.consumer;

import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.msghub.service.api.ConsumerDataHandler;
import com.jdddata.datahub.msghub.service.api.ConsumerServiceApi;
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
    public HubPullResult consumer(String s, String s1, String s2, Long l, Integer i) {
        return consumerServiceApi.pullConsumer(s, s1, s2, l, i);
    }

    @Override
    public boolean updateOffset(String s, String s1, String s2, String s3) {
        return consumerServiceApi.updateOffset(s, s1, s2, s3);
    }

    @Override
    public boolean start(String s, String s1, List<String> list) {
        try {
            return consumerServiceApi.start(s, s1, list);
        } catch (MQClientException e) {
            return false;
        }
    }
}
