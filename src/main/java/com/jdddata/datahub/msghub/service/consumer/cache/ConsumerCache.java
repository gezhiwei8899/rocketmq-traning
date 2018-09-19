package com.jdddata.datahub.msghub.service.consumer.cache;

import com.jdddata.datahub.msghub.common.Utils;
import com.jdddata.datahub.msghub.service.api.IConsumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ConsumerCache
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/18 15:03
 * @modified By:
 */
public class ConsumerCache {

    //Utils.consumerKey(type,groupName)
    private static final Map<String, IConsumer> CONSUMER_MAP = new ConcurrentHashMap<>();

    public static boolean exsit(String key) {
        return CONSUMER_MAP.containsKey(key);
    }

    public static void cacheConsumer(String type, String groupName, IConsumer iConsumer) {
        String key = Utils.consumerKey(type, groupName);
        CONSUMER_MAP.put(key, iConsumer);
    }

    public static Map<String, IConsumer> take() {
        return CONSUMER_MAP;
    }

    public static IConsumer getConsumerCache(String key) {
        return CONSUMER_MAP.get(key);
    }

}
