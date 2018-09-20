package com.jdddata.datahub.msghub.service.producer;

import com.jdddata.datahub.common.service.message.HubMessage;
import com.jdddata.datahub.msghub.common.MsghubConstants;
import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.MsgHubContext;
import com.jdddata.datahub.msghub.service.api.IProducer;
import com.jdddata.datahub.msghub.service.api.ProducerServiceApi;
import com.jdddata.datahub.msghub.service.producer.rocketmq.RocketmqProducer;
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
    public void initProducer(MsgHubContext msgHubContext) throws RocketMQException {

        if (null != msgHubContext.getNamesvr()) {
            IProducer iSender = new RocketmqProducer(msgHubContext.getNamesvr(), msgHubContext.getRocketGroupName());
            iSender.start();
            SENDER_MAP.put(MsghubConstants.ROCKET_MQ, iSender);
        }
        if (null != msgHubContext.getKafkaBroker()) {
            //TODO
        }
        setStartable(true);
    }
}
