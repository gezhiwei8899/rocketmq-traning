package com.jdddata.datahub.msghub.service.producer.rocketmq;

import com.alibaba.fastjson.JSON;
import com.jdddata.datahub.common.service.message.HubMessage;
import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.RocketMqContext;
import com.jdddata.datahub.msghub.service.api.IProducer;
import com.jdddata.datahub.msghub.service.consumer.Utils;
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
public class RocketmqProducer implements IProducer {

    private DefaultMQProducer mqProducer;

    private boolean send;


    @Override
    public void start(RocketMqContext msgHubConfig) throws RocketMQException {
        if (StringUtils.isBlank(msgHubConfig.getProducerGroupname())) {
            throw new RocketMQException("groupName is blank");
        }
        if (StringUtils.isBlank(msgHubConfig.getNamesvr())) {
            throw new RocketMQException("nameServerAddr is blank");
        }

        mqProducer = new DefaultMQProducer(msgHubConfig.getProducerGroupname());
        mqProducer.setNamesrvAddr(msgHubConfig.getNamesvr());
        //TODO 了解参数
//        mqProducer.setMaxMessageSize(msgHubConfig.getMaxMessageSize());
//        mqProducer.setSendMsgTimeout(msgHubConfig.getSendMsgTimeout());
        try {
            mqProducer.start();
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
    public boolean send(String namespace, String schema, String table, HubMessage message) {
        try {
            //TODO bytes复用
            byte[] msgBtyes = JSON.toJSONBytes(message);

            String topic = Utils.generateConsumerKey(namespace, schema, table);

            Message msg = new Message(topic, message.getTable(), "POS" + message.getBinlogPosition(), msgBtyes);

            SendResult sendResult = mqProducer.send(msg);
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
