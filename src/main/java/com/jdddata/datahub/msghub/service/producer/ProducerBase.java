package com.jdddata.datahub.msghub.service.producer;

import com.jdddata.datahub.common.service.message.HubMessage;
import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.RocketMqContext;
import com.jdddata.datahub.msghub.service.api.IProducer;
import com.jdddata.datahub.msghub.service.api.ProducerServiceApi;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ProducerBase
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 10:18
 * @modified By:
 */
@Service
public class ProducerBase implements ProducerServiceApi {

    private static final Map<String, IProducer> SENDER_MAP = new ConcurrentHashMap<>();

    private volatile boolean startable = false;

    @Override
    public void close() {
        SENDER_MAP.values().forEach(sender -> {
            try {
                sender.close();
            } catch (IOException e) {
                //ignore
            }
        });
    }


    @Override
    public boolean send(HubMessage message) {
        boolean isSend = true;
        for (IProducer sender : SENDER_MAP.values()) {
            isSend = sender.send(message.getNamespace(), message.getSchema(), message.getTable(), message);
        }
        return isSend;
    }

    @Override
    public boolean isStartable() {
        return startable;
    }

    @Override
    public void setStartable(boolean startable) {
        this.startable = startable;
    }

    @Override
    public void startProducerMsgHub(RocketMqContext rocketMqContext) throws RocketMQException {
        IProducer iSender = ProducerFactory.createSend("rocketmq");
        iSender.start(rocketMqContext);
        SENDER_MAP.put("rocketmq", iSender);
        setStartable(true);
    }
}
