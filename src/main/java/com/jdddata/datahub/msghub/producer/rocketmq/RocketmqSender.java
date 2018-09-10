package com.jdddata.datahub.msghub.producer.rocketmq;

import com.alibaba.fastjson.JSON;
import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.common.TopicMgr;
import com.jdddata.datahub.msghub.config.MQInfo;
import com.jdddata.datahub.msghub.producer.ISender;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @ClassName: Producer
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/7 18:07
 * @modified By:
 */
public class RocketmqSender implements ISender {

    private DefaultMQProducer mqProducer;

    private boolean send;


    @Override
    public void start(MQInfo msgHubConfig) throws RocketMQException {
        if (StringUtils.isBlank(msgHubConfig.getGroupName())) {
            throw new RocketMQException("groupName is blank");
        }
        if (StringUtils.isBlank(msgHubConfig.getNamesrvAddr())) {
            throw new RocketMQException("nameServerAddr is blank");
        }
        if (StringUtils.isBlank(msgHubConfig.getInstanceName())) {
            throw new RocketMQException("instanceName is blank");
        }
        mqProducer = new DefaultMQProducer(msgHubConfig.getGroupName());
        mqProducer.setNamesrvAddr(msgHubConfig.getNamesrvAddr());
        mqProducer.setInstanceName(msgHubConfig.getInstanceName());
        mqProducer.setMaxMessageSize(msgHubConfig.getMaxMessageSize());
        mqProducer.setSendMsgTimeout(msgHubConfig.getSendMsgTimeout());
        try {
            mqProducer.start();
            System.out.println("producer启动成功");
        } catch (MQClientException e) {
            throw new RocketMQException("producer启动失败", e);
        }
        addShutdownHook();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (null != mqProducer) {
                    mqProducer.shutdown();
                }
            } catch (Exception e) {
                // Ignore
            }
        }));
    }

    @Override
    public boolean send(String namespace, String schema, com.jdddata.datahub.common.service.Message message) {
        try {
            byte[] msgBtyes = JSON.toJSONBytes(message);

            Message msg = new Message(TopicMgr.parseTopic(namespace, schema), message.getTable(), "POS" + message.getBinlogPosition(), msgBtyes);

            SendResult sendResult = mqProducer.send(msg, new SelectMessageQueueByHash(), message.getTable());
            if (sendResult == null || !SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                return false;
            }
        } catch (InterruptedException | MQBrokerException | MQClientException | RemotingException e) {
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        if (null != mqProducer) {
            mqProducer.shutdown();
        }
    }
}
