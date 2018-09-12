package com.jdddata.datahub.msghub.service;


import com.alibaba.dubbo.config.annotation.Service;
import com.codahale.metrics.Meter;
import com.jdddata.datahub.common.service.message.Message;
import com.jdddata.datahub.common.service.producer.ProducerDataService;
import com.jdddata.datahub.common.service.producer.Result;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.ProducerDataHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: ProducerDataServiceImpl
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/7 16:46
 * @modified By:
 */
@Service(timeout = 5000)
public class ProducerDataServiceImpl implements ProducerDataService {

    private final Meter requests = Metrics.defaultRegistry().meter("ProducerDataServiceImpl");

    @Autowired
    private ProducerDataHandler producerDataHandler;

    @Override
    public Result produce(String namespace, String schema, Message message) {
        requests.mark();
        Result result = new Result();
        if (producerDataHandler.store(message)) {

            result.setCode(0);
            result.setMessage("");
            return result;
        }
        result.setMessage("error");
        result.setCode(1);
        return result;
    }
}
