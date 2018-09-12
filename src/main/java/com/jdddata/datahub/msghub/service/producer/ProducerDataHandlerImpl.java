package com.jdddata.datahub.msghub.service.producer;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.jdddata.datahub.common.service.message.Message;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.ProducerDataHandler;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName: ReceiveImpl
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 9:52
 * @modified By:
 */
@Service
public class ProducerDataHandlerImpl implements ProducerDataHandler {

    private static final BlockingQueue<Message> MESSAGE_BLOCKING_QUEUE = new LinkedBlockingQueue<>(100000);

    static {
        Metrics.defaultRegistry().register(MetricRegistry.name(ProducerDataHandlerImpl.class, "name", "size"), (Gauge<Long>) () -> (long) MESSAGE_BLOCKING_QUEUE.size());
    }

    @Override
    public boolean store(Message message) {
        return MESSAGE_BLOCKING_QUEUE.offer(message);
    }

    @Override
    public Message take() throws InterruptedException {
        return MESSAGE_BLOCKING_QUEUE.take();
    }


}
