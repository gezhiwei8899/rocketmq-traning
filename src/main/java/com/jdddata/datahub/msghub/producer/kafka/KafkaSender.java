package com.jdddata.datahub.msghub.producer.kafka;

import com.jdddata.datahub.common.service.Message;
import com.jdddata.datahub.msghub.config.MQInfo;
import com.jdddata.datahub.msghub.producer.ISender;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @ClassName: KafkaSender
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 12:38
 * @modified By:
 */
public class KafkaSender implements ISender {

    private Producer producer;

    @Override
    public void start(MQInfo msgHubConfig) {

    }

    @Override
    public boolean send(String namespace, String schema, Message message) {
        try {
            ProducerRecord value = null;
            Future<RecordMetadata> send = producer.send(null);
            RecordMetadata recordMetadata = send.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public void close() {

    }
}
