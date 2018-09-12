package com.jdddata.datahub.msghub.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.codahale.metrics.Meter;
import com.jdddata.datahub.common.service.consumer.ConsumerDataService;
import com.jdddata.datahub.common.service.consumer.PullResult;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.ConsumerDataHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName: ConsumerDataServiceImpl
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/11 11:14
 * @modified By:
 */
@Service(timeout = 5000)
public class ConsumerDataServiceImpl implements ConsumerDataService {

    private final Meter requests = Metrics.defaultRegistry().meter("ConsumerDataServiceImpl");
    @Autowired
    private ConsumerDataHandler consumerDataHandler;

    @Override
    public PullResult subscribe(String s, String s1, String s2, long l, int i) {
        requests.mark();
        return consumerDataHandler.consumer(s, s1, s2,l, i);
    }

    @Override
    public boolean commit(String s, String s1, String s2, String s3) {
        return consumerDataHandler.updateOffset(s, s1, s2, s3);
    }
}
