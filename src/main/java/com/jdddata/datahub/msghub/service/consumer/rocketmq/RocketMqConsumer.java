package com.jdddata.datahub.msghub.service.consumer.rocketmq;

import com.alibaba.fastjson.JSON;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.jdddata.datahub.common.service.consumer.HubMessageExt;
import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.common.service.message.HubMessage;
import com.jdddata.datahub.msghub.config.RocketMqContext;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.IConsumer;
import com.jdddata.datahub.msghub.service.consumer.Utils;
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


    //缓存 一个key 一个缓存队列。最多存储一万条
    private static final Map<String, BlockingQueue<MessageCache>> STRING_BLOCKING_QUEUE_MAP = new ConcurrentHashMap<>();

    //MessageQueue与offset的关系表
    private static final Map<MessageQueue, Long> OFFSE_TABLE = new ConcurrentHashMap<>();

    //key和MessageQueue
    private static final Map<String, MessageQueue> STRING_MESSAGE_QUEUE_MAP = new ConcurrentHashMap<>();


    private boolean running;

    private DefaultMQPullConsumer consumer;

    private String groupName;

    private String topic;

    private String key;

    private RocketMqContext rocketMqContext;

    public RocketMqConsumer(String s1, String s2, RocketMqContext rocketMqContext) {
        this.groupName = s1;
        this.consumer = new DefaultMQPullConsumer(s1);
        this.topic = s2;
        this.key = Utils.generateConsumerKey("rokcetmq", s1, s2);
        this.rocketMqContext = rocketMqContext;
        initMetrics();

    }

    /**
     *
     */
    private void initMetrics() {
        SortedMap<String, Gauge> gauges = Metrics.defaultRegistry().getGauges();
        String topsize = MetricRegistry.name(RocketMqConsumer.class, "key and messagequeque", "size");
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
    public HubPullResult pullMessage() {
        return this.pullMessage(null, 32);
    }

    @Override
    public HubPullResult pullMessage(Long offset, Integer max) {


        //TODO 暂时不考虑offset
        BlockingQueue<MessageCache> messageCaches = STRING_BLOCKING_QUEUE_MAP.get(key);
        if (null == messageCaches) {
            return null;
        }

        int num = null != max ? max : 32;

        List<MessageCache> messageCacheList = new ArrayList<>(num);
        List<HubMessageExt> messageExts = new ArrayList<>(num);

        int i = messageCaches.drainTo(messageCacheList, num);
        if (i < 0) {
            return null;
        }

        long maxOffset = 0;
        long minoffset = 0;
        long nextoffset = 0;

        for (int j = 0; j < messageCacheList.size(); j++) {
            if (j == messageCacheList.size() - 1) {
                MessageCache messageCache = messageCacheList.get(j);
                nextoffset = messageCache.getNextBeginOffset();
                minoffset = messageCache.getMinOffset();
                maxOffset = messageCache.getMaxOffset();
            }
            messageExts.add(messageCacheList.get(j).getHubMessageExt());
        }
        return new HubPullResult(topic, nextoffset, minoffset, maxOffset, messageExts);
    }

    @Override
    public boolean updateOffset(String type, String groupName, String topic, Long offset) {
        String remotekey = Utils.generateConsumerKey(type, groupName, topic);
        return updateOffset(remotekey, offset);
    }

    private boolean updateOffset(String keys, Long offset) {
        try {
            MessageQueue messageQueue = STRING_MESSAGE_QUEUE_MAP.get(keys);
            if (null == messageQueue) {
                return false;
            }
            consumer.updateConsumeOffset(messageQueue, offset);
        } catch (Exception e) {
            return false;
        }
        return false;

    }

    @Override
    public void start() throws MQClientException {
        consumer.setNamesrvAddr(rocketMqContext.getNamesvr());
        consumer.start();
    }

    @Override
    public void clear() {

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

    private synchronized boolean isRunning() {
        return running;
    }

    private synchronized void setRunning(boolean running) {
        this.running = running;
    }

    private void putKeyQueue(MessageQueue mq) {
        STRING_MESSAGE_QUEUE_MAP.put(key, mq);
    }


    @Override
    public void run() {
        try {
            Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topic);
            for (MessageQueue mq : mqs) {
                while (true) {
                    /**
                     * MessageQueue只有一个，只要消费这个就可以了
                     */
                    SINGLE_MQ:
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
//            consumer.shutdown();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    private void process(PullResult pullResult) {

        long maxOffset = pullResult.getMaxOffset();
        long minOffset = pullResult.getMinOffset();
        long nextBeginOffset = pullResult.getNextBeginOffset();

        BlockingQueue<MessageCache> messageCaches = fetchMessageCacheQueue(key);

        for (MessageExt messageExt : pullResult.getMsgFoundList()) {
            HubMessage hubMessage = JSON.parseObject(messageExt.getBody(), HubMessage.class);
            messageCaches.offer(new MessageCache(topic, maxOffset, minOffset, nextBeginOffset, hubMessage, messageExt));
        }
        STRING_BLOCKING_QUEUE_MAP.put(key, messageCaches);
    }

    private BlockingQueue<MessageCache> fetchMessageCacheQueue(String key) {
        BlockingQueue<MessageCache> messageCaches = STRING_BLOCKING_QUEUE_MAP.get(key);
        if (null == messageCaches) {
            messageCaches = new LinkedBlockingQueue<>(10000);
        }
        return messageCaches;
    }

}
