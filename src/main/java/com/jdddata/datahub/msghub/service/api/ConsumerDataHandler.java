package com.jdddata.datahub.msghub.service.api;

import com.jdddata.datahub.common.service.consumer.HubPullResult;

import java.util.List;

/**
 * @ClassName: ConsumerDataHandler
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 10:04
 * @modified By:
 */
public interface ConsumerDataHandler {
    HubPullResult consumer(String s, String s1, String s2, Long l, Integer i);

    boolean updateOffset(String s, String s1, String s2, String s3);

    boolean start(String s, String s1, List<String> list);
}
