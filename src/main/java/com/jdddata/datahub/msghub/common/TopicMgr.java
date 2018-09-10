package com.jdddata.datahub.msghub.common;

/**
 * @ClassName: TopicMgr
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 13:39
 * @modified By:
 */
public class TopicMgr {
    public static String parseTopic(String namespace, String schema) {
        return namespace + "_" + schema;
    }
}
