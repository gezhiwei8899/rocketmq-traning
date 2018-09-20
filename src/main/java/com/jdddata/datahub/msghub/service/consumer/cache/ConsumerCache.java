package com.jdddata.datahub.msghub.service.consumer.cache;

import com.jdddata.datahub.msghub.common.Utils;
import com.jdddata.datahub.msghub.service.api.IConsumer;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: ConsumerCache
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/18 15:03
 * @modified By:
 */
public class ConsumerCache {

    //Utils.consumerKey(type,groupName)
    private static final Map<String, ConsumerUnit> CONSUMER_MAP = new ConcurrentHashMap<>();
    private static final BlockingQueue<IConsumer> I_CONSUMERS = new LinkedBlockingQueue<>(10000);

    public static boolean exsit(String key) {
        return CONSUMER_MAP.containsKey(key);
    }


    public static IConsumer take() throws InterruptedException {

        return I_CONSUMERS.poll(2, TimeUnit.SECONDS);
    }


    public static void cacheConsumerNewConnection(String key, Connection connection) {
        ConsumerUnit consumerUnit = CONSUMER_MAP.get(key);
        consumerUnit.add(connection);
    }

    public static void cacheConsumer(String type, String groupName, IConsumer iConsumer, Connection connection) {
        String key = Utils.consumerKey(type, groupName);
        CONSUMER_MAP.put(key, new ConsumerUnit(key, iConsumer, connection));
    }

    public static ConsumerUnit getConsumerCache(String key) {
        return CONSUMER_MAP.get(key);
    }

    public static void put() {
        CONSUMER_MAP.values().forEach(consumerUnit -> I_CONSUMERS.offer(consumerUnit.getiConsumer()));
    }
}
