package com.jdddata.datahub.msghub.service.consumer;

import com.jdddata.datahub.common.service.consumer.PullResult;
import com.jdddata.datahub.msghub.common.TopicMgr;
import com.jdddata.datahub.msghub.service.api.ConsumerServiceApi;
import com.jdddata.datahub.msghub.service.api.IConsumer;
import org.springframework.stereotype.Service;

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

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);


    @Override
    public boolean start(String s, String s1, String s2, long l, int i) {
        String key = TopicMgr.parseMesageCacheKey(s, s1, s2);
        IConsumer iConsumer = STRING_ROCKET_CONSUMER_TASK_MAP.get(key);
        if (null == iConsumer || !iConsumer.isRunninged()) {
            iConsumer = ConsumerFactory.createInstance(s, s1, s2);
            executorService.submit(iConsumer);
            iConsumer.setRunninged(true);
            STRING_ROCKET_CONSUMER_TASK_MAP.put(key, iConsumer);
        }
        return true;
    }

    @Override
    public PullResult pullConsumer(String s, String s1, String s2, long l, int i) {
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
}
