package com.jdddata.datahub.msghub.producer;

import com.jdddata.datahub.common.service.Message;
import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.MQInfo;
import com.jdddata.datahub.msghub.config.MsgHubConfig;
import com.jdddata.datahub.msghub.producer.rocketmq.RocketmqSender;
import org.apache.commons.lang3.StringUtils;

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
public enum ProducerBase {

    INSTANCE,
    ;

    private static final Map<String, ISender> SENDER_MAP = new ConcurrentHashMap<>();

    private volatile boolean startable = false;



    private MsgHubConfig msgHubConfig;

    private List<MQInfo> mqInfos;

    public static void close() {
        SENDER_MAP.values().forEach(sender -> {
            try {
                sender.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void init(MsgHubConfig msgHubConfig) throws RocketMQException {
        this.msgHubConfig = msgHubConfig;
        this.mqInfos = msgHubConfig.getMqInfoList();
        register();
    }


    public void register() throws RocketMQException {
        List<MQInfo> mqInfoList = msgHubConfig.getMqInfoList();
        for (MQInfo mqInfo : mqInfoList) {
            ISender iSender = createSend(mqInfo);

            SENDER_MAP.put(mqInfo.getMqName(), iSender);
        }
    }


    private static ISender createSend(MQInfo mqInfo) throws RocketMQException {
        ISender iSender = null;
        String mqName = mqInfo.getMqName();
        if (StringUtils.equalsIgnoreCase(mqName, "kafka")) {

        }
        if (StringUtils.equalsIgnoreCase(mqName, "rocektmq")) {
            iSender = new RocketmqSender();
        }
        iSender = new RocketmqSender();
        iSender.start(mqInfo);
        return iSender;
    }

    public boolean send(String namespace, String schema, Message message) {
        boolean isSend = true;
        for (ISender sender : SENDER_MAP.values()) {
            isSend = sender.send(namespace, schema, message);
        }
        return isSend;
    }

    public boolean isStartable() {
        return startable;
    }

    public void setStartable(boolean startable) {
        this.startable = startable;
    }
}
