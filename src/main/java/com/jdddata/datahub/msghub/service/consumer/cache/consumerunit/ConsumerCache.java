package com.jdddata.datahub.msghub.service.consumer.cache.consumerunit;

import com.jdddata.datahub.msghub.service.api.IConsumer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ConsumerCache
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/14 14:07
 * @modified By:
 */
public class ConsumerCache {
    //key: Connection ConnectionCache#Map<String, List<String>>
    private static final Map<String, ConsumerUnit> consumerCacheMap = new ConcurrentHashMap<>();


    public static List<String> backNotInit(List<String> consumerKeys) {
        //TODO
        Set<String> commons = new TreeSet<>();
        for (String consumerKey : consumerKeys) {
            for (String key : consumerCacheMap.keySet()) {
                if (StringUtils.equalsIgnoreCase(consumerKey, key)) {
                    commons.add(key);
                }
            }
        }
        Iterator<String> iterator = consumerKeys.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            for (String common : commons) {
                if (StringUtils.equalsIgnoreCase(next, common)) {
                    iterator.remove();
                }
            }
        }
        return consumerKeys;
    }

    public static boolean putConsumerToCache(String s, IConsumer iConsumer, String uuid) {
        ConsumerUnit consumerUnit = consumerCacheMap.get(s);
        if (null == consumerUnit) {
            consumerUnit = new ConsumerUnit(s, iConsumer, uuid);
            consumerCacheMap.put(s, consumerUnit);
            return true;
        }
        List<String> bindings = consumerUnit.getBindings();
        if (!bindings.contains(uuid)) {
            bindings.add(uuid);
        }
        consumerCacheMap.put(s, consumerUnit);
        return true;
    }

    public static ConsumerUnit getConsumerUnit(String s) {
        return consumerCacheMap.get(s);
    }
}
