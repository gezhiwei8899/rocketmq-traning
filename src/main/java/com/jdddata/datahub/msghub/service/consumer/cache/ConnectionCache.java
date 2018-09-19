package com.jdddata.datahub.msghub.service.consumer.cache;

import com.jdddata.datahub.msghub.common.MsgHubConnectionExcepiton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ConnectionCache
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/18 14:58
 * @modified By:
 */
public class ConnectionCache {
    private static final Map<String, Connection> CONNECTION_MAP = new ConcurrentHashMap<>();

    public static Connection getConnection(String uuid) {
        return CONNECTION_MAP.get(uuid);
    }

    public static void cacheConnection(String uuid, String type, String groupName, List<String> topics) {
        CONNECTION_MAP.put(uuid, new Connection(uuid, type, groupName, topics));
    }

    public static void refreshIdleTime(String uuid) throws MsgHubConnectionExcepiton {
        Connection connection = CONNECTION_MAP.get(uuid);
        if (null == connection) {
            throw new MsgHubConnectionExcepiton(uuid + " connection is not exsit");
        }
        connection.refresh();
    }
}
