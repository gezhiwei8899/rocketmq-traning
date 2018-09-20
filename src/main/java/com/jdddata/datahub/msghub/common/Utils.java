package com.jdddata.datahub.msghub.common;

import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: Utils
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/14 15:28
 * @modified By:
 */
public class Utils {

    public static String consumerKey(String type, String groupName) {
        type = null != type ? type : MsghubConstants.ROCKET_MQ;
        return type + "_" + groupName;
    }

    public static String producerKey(String namespace, String schema, String table) {
        if (StringUtils.isBlank(namespace)) {
            namespace = "default";
        }
        return namespace + "_" + schema + "_" + table;
    }
}
