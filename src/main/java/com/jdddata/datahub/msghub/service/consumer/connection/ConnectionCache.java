package com.jdddata.datahub.msghub.service.consumer.connection;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ConnectionCache
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/14 14:16
 * @modified By:
 */
public class ConnectionCache {

    //key: uuid
    private static final Map<String, Connection> CONNECTION_MAP = new ConcurrentHashMap<>();

    private static final Map<String, List<String>> Ref_CONSUMERS_MAP = new ConcurrentHashMap<>();


    public static Connection getConnection(String uuid) {
        return CONNECTION_MAP.get(uuid);
    }


    public static void putConnectionCache(String uuid, List<String> cacheKey) {
        String s = UUID.randomUUID().toString();
        Ref_CONSUMERS_MAP.put(uuid, cacheKey);
        CONNECTION_MAP.put(uuid, new Connection(uuid, s, new Date()));
    }

    public static void refreshIdleTime(String uuid) {
        Connection connection = CONNECTION_MAP.get(uuid);
        if (null == connection) {
            //throw
        }
        connection.refresh();
    }
}
