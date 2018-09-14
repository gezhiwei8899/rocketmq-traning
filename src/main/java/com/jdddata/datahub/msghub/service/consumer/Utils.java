package com.jdddata.datahub.msghub.service.consumer;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Utils
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/14 15:28
 * @modified By:
 */
public class Utils {

    /**
     * 找出所有的key 和 consumer 一一对应的key
     *
     * @param type
     * @param groupName
     * @param topics
     * @return
     */
    public static List<String> generateConsumerKeys(String type, String groupName, List<String> topics) {
        List<String> strings = new ArrayList<>();
        for (String topic : topics) {
            strings.add(generateConsumerKey(type, groupName, topic));
        }
        return strings;
    }

    public static String generateConsumerKey(String s, String s1, String s2) {
        if (null == s) {
            s = "rocketmq";
        }
        return s + "_" + s1 + "_" + s2;
    }
}
