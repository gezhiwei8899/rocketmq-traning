package com.jdddata.datahub.msghub.service.consumer.rocketmq;

import com.alibaba.fastjson.JSON;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.jdddata.datahub.common.service.message.Message;
import com.jdddata.datahub.msghub.common.TopicMgr;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.IConsumer;
import com.jdddata.datahub.msghub.service.consumer.cache.MessageCache;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ClassName: RocketMqConsumer
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/11 11:35
 * @modified By:
 */
public class RocketMqConsumer implements IConsumer {


    private static final Map<String, BlockingQueue<MessageCache>> STRING_BLOCKING_QUEUE_MAP = new ConcurrentHashMap<>();

    private static final Map<MessageQueue, Long> OFFSE_TABLE = new ConcurrentHashMap<>();

    private static final Map<String, MessageQueue> STRING_MESSAGE_QUEUE_MAP = new ConcurrentHashMap<>();


    private boolean running;

    private DefaultMQPullConsumer consumer;

    private String groupName;

    private String topic;

    private String key;

    public RocketMqConsumer(String s1, String s2) {
        this.groupName = s1;
        this.consumer = new DefaultMQPullConsumer(s1);
        this.topic = s2;
        this.key = TopicMgr.parseMesageCacheKey("rokcetmq", s1, s2);
        initMetrics();

    }

    private void initMetrics() {

        SortedMap<String, Gauge> gauges = Metrics.defaultRegistry().getGauges();
        String topsize = MetricRegistry.name(RocketMqConsumer.class, "STRING_MESSAGE_QUEUE_MAP", "size");
        if (!gauges.containsKey(topsize)) {
            Metrics.defaultRegistry().register(topsize, (Gauge<Long>) () -> (long) STRING_MESSAGE_QUEUE_MAP.size());
        }

        for (Map.Entry<String, BlockingQueue<MessageCache>> stringBlockingQueueEntry : STRING_BLOCKING_QUEUE_MAP.entrySet()) {
            String cacheSize = MetricRegistry.name(RocketMqConsumer.class, "STRING_BLOCKING_QUEUE_MAP", stringBlockingQueueEntry.getKey(), "size");
            if (!gauges.containsKey(cacheSize)) {
                Metrics.defaultRegistry().register(cacheSize, (Gauge<Long>) () -> (long) stringBlockingQueueEntry.getValue().size());
            }
        }
    }


    @Override
    public boolean isRunninged() {
        return isRunning();
    }

    @Override
    public void setRunninged(boolean b) {
        setRunning(b);
    }

    @Override
    public com.jdddata.datahub.common.service.consumer.PullResult pullMessage() throws InterruptedException {
        return this.pullMessage(null, 32);
    }

    @Override
    public com.jdddata.datahub.common.service.consumer.PullResult pullMessage(Long offset, Integer max) {
        List<MessageCache> messageCacheList = new ArrayList<>(max);
        List<Message> messages = new ArrayList<>(max);
        BlockingQueue<MessageCache> messageCaches = STRING_BLOCKING_QUEUE_MAP.get(key);
        if (null == messageCaches) {
            return null;
        }
        int i = messageCaches.drainTo(messageCacheList, max);
        if (i < 0) {
            return null;
        }
        long maxOffset = 0;
        long minoffset = 0;
        long nexoffset = 0;
        for (int j = 0; j < messageCacheList.size(); j++) {
            if (j == messageCacheList.size()) {
                MessageCache messageCache = messageCacheList.get(j);
                nexoffset = messageCache.getNextBeginOffset();
                minoffset = messageCache.getMinOffset();
                maxOffset = messageCache.getMaxOffset();
            }
            messages.add(messageCacheList.get(j).getMessage());
        }
        com.jdddata.datahub.common.service.consumer.PullResult pullResult = new com.jdddata.datahub.common.service.consumer.PullResult(topic, nexoffset, minoffset, maxOffset, messages);
        return pullResult;
    }

    @Override
    public boolean updateOffset(String s, String s1, String s2, String s3) {
        try {
            long l = Long.parseLong(s3);
            MessageQueue messageQueue = STRING_MESSAGE_QUEUE_MAP.get(topic);
            if (null == messageQueue) {
                return false;
            }
            consumer.updateConsumeOffset(messageQueue, l);
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static long getMessageQueueOffset(MessageQueue mq) {
        Long offset = OFFSE_TABLE.get(mq);
        if (offset != null)
            return offset;

        return 0;
    }

    private static void putMessageQueueOffset(MessageQueue mq, long offset) {
        OFFSE_TABLE.put(mq, offset);
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    private void putKeyQueue(MessageQueue mq) {
        STRING_MESSAGE_QUEUE_MAP.put(key, mq);
    }

    private void process(PullResult pullResult) {
        long maxOffset = pullResult.getMaxOffset();
        long minOffset = pullResult.getMinOffset();
        long nextBeginOffset = pullResult.getNextBeginOffset();
        long num = 32l;
        BlockingQueue<MessageCache> messageCaches = STRING_BLOCKING_QUEUE_MAP.get(key);
        if (null == messageCaches) {
            messageCaches = new LinkedBlockingQueue<>(10000);
        }
        List<MessageExt> messageExts = pullResult.getMsgFoundList();
        for (MessageExt messageExt : messageExts) {
            Message message = JSON.parseObject(messageExt.getBody(), Message.class);
            messageCaches.offer(new MessageCache(topic, maxOffset, minOffset, (nextBeginOffset - num), message));
            num--;
        }
        STRING_BLOCKING_QUEUE_MAP.put(key, messageCaches);
    }

    @Override
    public void run() {
        try {
            Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topic);
            for (MessageQueue mq : mqs) {
                System.out.printf("Consume from the queue: %s%n", mq);
                SINGLE_MQ:
                while (true) {
                    try {
                        PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
                        putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
                        putKeyQueue(mq);
                        switch (pullResult.getPullStatus()) {
                            case FOUND:
                                process(pullResult);
                                break;
                            case NO_MATCHED_MSG:
                                break;
                            case NO_NEW_MSG:
                                break SINGLE_MQ;
                            case OFFSET_ILLEGAL:
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            consumer.shutdown();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

}
