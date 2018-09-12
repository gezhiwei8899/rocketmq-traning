package com.jdddata.datahub.msghub.service.api;

import com.jdddata.datahub.common.service.consumer.PullResult;

/**
 * @InterfaceName: ConsumerServiceApi
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 10:11
 * @modified By:
 */
public interface ConsumerServiceApi {
    boolean start(String s, String s1, String s2, long l, int i);

    PullResult pullConsumer(String s, String s1, String s2, long l, int i);

    boolean updateOffset(String s, String s1, String s2, String s3);
}
