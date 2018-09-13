package com.jdddata.datahub.msghub.service.consumer;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.msghub.common.TopicMgr;
import com.jdddata.datahub.msghub.config.RocketMqContext;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.ConsumerServiceApi;
import com.jdddata.datahub.msghub.service.api.IConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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


    private static final Map<String, IConsumer> STRING_ROCKET_CONSUMER_TASK_MAP = new ConcurrentHashMap<>();

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10, r -> new Thread(r, "pullMessageFromMQ"));

    @Autowired
    private RocketMqContext rocketMqContext;

    static {
        Metrics.defaultRegistry().register(MetricRegistry.name(ConsumerBase.class, "ConsumerBase IConsumer", "size"), (Gauge<Long>) () -> (long) STRING_ROCKET_CONSUMER_TASK_MAP.size());
    }

    @Override
    public boolean start(String s, String s1, String s2, Long l, Integer i) throws MQClientException {
        String key = TopicMgr.parseMesageCacheKey(s, s1, s2);
        IConsumer iConsumer = STRING_ROCKET_CONSUMER_TASK_MAP.get(key);
        startConsumer(s, s1, s2, key, iConsumer);
        return true;
    }

    @Override
    public HubPullResult pullConsumer(String s, String s1, String s2, Long l, Integer i) {
        String key = TopicMgr.parseMesageCacheKey(s, s1, s2);
        IConsumer iConsumer = STRING_ROCKET_CONSUMER_TASK_MAP.get(key);
        return iConsumer.pullMessage(l, i);
    }

    @Override
    public boolean updateOffset(String s, String s1, String s2, String s3) {
        String key = TopicMgr.parseMesageCacheKey(s, s1, s2);
        IConsumer iConsumer = STRING_ROCKET_CONSUMER_TASK_MAP.get(key);
        if (null == iConsumer) {
            return false;
        }
        return iConsumer.updateOffset(s, s1, s2, s3);
    }

    @Override
    public boolean start(String s, String s1, List<String> list) throws MQClientException {
        for (String s2 : list) {
            String key = TopicMgr.parseMesageCacheKey(s, s1, s2);
            IConsumer iConsumer = STRING_ROCKET_CONSUMER_TASK_MAP.get(key);
            startConsumer(s, s1, s2, key, iConsumer);
        }
        return false;
    }

    private void startConsumer(String s, String s1, String s2, String key, IConsumer iConsumer) throws MQClientException {
        if (null == iConsumer || !iConsumer.isRunninged()) {
            iConsumer = ConsumerFactory.createInstance(s, s1, s2, rocketMqContext);
            iConsumer.start();
            executorService.submit(iConsumer);
            iConsumer.setRunninged(true);
            STRING_ROCKET_CONSUMER_TASK_MAP.put(key, iConsumer);
        } else if (!iConsumer.isRunninged()) {
            iConsumer.start();
            executorService.submit(iConsumer);
            iConsumer.setRunninged(true);
            STRING_ROCKET_CONSUMER_TASK_MAP.put(key, iConsumer);
        } else {
            iConsumer.clear();
        }
    }
}
