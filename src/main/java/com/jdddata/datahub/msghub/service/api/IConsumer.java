package com.jdddata.datahub.msghub.service.api;

import com.jdddata.datahub.common.service.consumer.PullResult;

/**
 * @ClassName: IConsumer
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 10:18
 * @modified By:
 */
public interface IConsumer extends Runnable {

    boolean isRunninged();

    void setRunninged(boolean b);

    PullResult pullMessage() throws InterruptedException;

    PullResult pullMessage(Long offset, Integer max);

    boolean updateOffset(String s, String s1, String s2, String s3);
}
