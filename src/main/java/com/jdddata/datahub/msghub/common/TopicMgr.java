package com.jdddata.datahub.msghub.common;

/**
 * @ClassName: TopicMgr
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 13:39
 * @modified By:
 */
public class TopicMgr {
    public static String parseTopic(String namespace, String schema, String table) {
        return namespace + "_" + schema + "_" + table;
    }

    public static String parseMesageCacheKey(String s, String s1, String s2) {
        if (null==s) {
            s = "rocketmq";
        }
        return s + "_" + s1 + "_" + s2;
    }
}
