package com.jdddata.datahub.msghub.service.consumer.particle;

import com.alibaba.fastjson.JSON;
import com.jdddata.datahub.common.service.consumer.HubMessageExt;
import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.common.service.consumer.HubPullStats;
import com.jdddata.datahub.common.service.message.HubMessage;
import com.jdddata.datahub.msghub.service.consumer.cache.MessageCache;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName: ConsumerParticle
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/20 14:43
 * @modified By:
 */
public class ConsumerParticle implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(ConsumerParticle.class);

    private final DefaultMQPullConsumer consumer;

    private final String type;

    private final String groupName;

    private final String topic;

    private final BlockingQueue<MessageCache> blockingQueue;

    private volatile Long offset;

    private volatile boolean running = true;


    public ConsumerParticle(DefaultMQPullConsumer consumer, String type, String groupName, String topic, Long cacheSize, Long offset) throws MQClientException {
        this.consumer = consumer;
        this.type = type;
        this.groupName = groupName;
        this.topic = topic;
        this.blockingQueue = new LinkedBlockingQueue<>();
        this.offset = offset;
        init(consumer, topic);
    }

    private void init(DefaultMQPullConsumer consumer, String topic) throws MQClientException {
        Set<MessageQueue> messageQueues = consumer.fetchSubscribeMessageQueues(topic);
        if (messageQueues.size() > 1) {
            throw new MQClientException(10001, "这个topic创建不符合规定");
        }
        if (null != messageQueues || messageQueues.size() != 0) {
            MessageQueue mq = messageQueues.iterator().next();
            offset = consumer.fetchConsumeOffset(mq, true);
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                LOGGER.debug("topic:{} is running and offset is:{}", topic, offset);
                if (blockingQueue.size() > 1000) {
                    continue;
                }
                Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topic);
                MessageQueue mq = mqs.iterator().next();
                PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, offset, 16);
                offset = pullResult.getNextBeginOffset();
                switch (pullResult.getPullStatus()) {
                    case FOUND:
                        long maxOffset = pullResult.getMaxOffset();
                        long minOffset = pullResult.getMinOffset();
                        long nextBeginOffset = pullResult.getNextBeginOffset();
                        for (MessageExt messageExt : pullResult.getMsgFoundList()) {
                            HubMessage hubMessage = JSON.parseObject(messageExt.getBody(), HubMessage.class);
                            blockingQueue.offer(new MessageCache(topic, maxOffset, minOffset, nextBeginOffset, hubMessage, messageExt));
                        }
                        break;
                    case NO_MATCHED_MSG:
                        break;
                    case NO_NEW_MSG:
                        break;
                    case OFFSET_ILLEGAL:
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.debug("topic:{} is exception:e", topic, e);
        }
    }

    public HubPullResult pullMessges(Long offset, Integer max) {
        int num = (null != max && max < 32) ? max : 32;
        List<MessageCache> messageCacheList = new ArrayList<>(num);
        List<HubMessageExt> messageExts = new ArrayList<>(num);

        int i = blockingQueue.drainTo(messageCacheList, num);
        if (i <= 0) {
            LOGGER.debug("topic:{} draint is null", topic);
            return new HubPullResult(HubPullStats.NO_MESSAGE, null);
        }
        for (int j = 0; j < messageCacheList.size(); j++) {
            messageExts.add(messageCacheList.get(j).getHubMessageExt());
        }
        HubPullResult hubPullResult = new HubPullResult(HubPullStats.OK, messageExts);
        LOGGER.debug(hubPullResult.toString() + "topic is {}", topic);
        return hubPullResult;
    }

    public boolean commitOffset(Long offsets) throws MQClientException {
        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topic);
        MessageQueue mq = mqs.iterator().next();
        consumer.updateConsumeOffset(mq, offsets);
        LOGGER.debug("offsets:{} commit success and running offset is {}", offsets, offset);
        return true;
    }
}