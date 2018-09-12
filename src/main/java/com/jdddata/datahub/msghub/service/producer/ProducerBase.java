package com.jdddata.datahub.msghub.service.producer;

import com.jdddata.datahub.common.service.message.Message;
import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.MQInfo;
import com.jdddata.datahub.msghub.config.MsgHubConfig;
import com.jdddata.datahub.msghub.service.api.IProducer;
import com.jdddata.datahub.msghub.service.api.ProducerServiceApi;
import com.jdddata.datahub.msghub.service.producer.rocketmq.RocketmqProducer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
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


    private MsgHubConfig msgHubConfig;

    private List<MQInfo> mqInfos;

    @Override
    public void close() {
        SENDER_MAP.values().forEach(sender -> {
            try {
                sender.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init(MsgHubConfig msgHubConfig) throws RocketMQException {
        this.msgHubConfig = msgHubConfig;
        this.mqInfos = msgHubConfig.getMqInfoList();
        register();
    }


    private void register() throws RocketMQException {
        List<MQInfo> mqInfoList = msgHubConfig.getMqInfoList();
        for (MQInfo mqInfo : mqInfoList) {
            IProducer iSender = createSend(mqInfo);

            SENDER_MAP.put(mqInfo.getMqName(), iSender);
        }
    }


    private static IProducer createSend(MQInfo mqInfo) throws RocketMQException {
        IProducer iSender = null;
        String mqName = mqInfo.getMqName();
        if (StringUtils.equalsIgnoreCase(mqName, "kafka")) {

        }
        if (StringUtils.equalsIgnoreCase(mqName, "rocektmq")) {
            iSender = new RocketmqProducer();
        }
        iSender = new RocketmqProducer();
        iSender.start(mqInfo);
        return iSender;
    }

    @Override
    public boolean send(String namespace, String schema, Message message) {
        boolean isSend = true;
        for (IProducer sender : SENDER_MAP.values()) {
            isSend = sender.send(namespace, schema, message);
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
}
