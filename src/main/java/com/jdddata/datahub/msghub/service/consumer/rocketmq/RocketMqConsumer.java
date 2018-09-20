package com.jdddata.datahub.msghub.service.consumer.rocketmq;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.msghub.common.MsghubConstants;
import com.jdddata.datahub.msghub.common.Utils;
import com.jdddata.datahub.msghub.metric.Metrics;
import com.jdddata.datahub.msghub.service.api.IConsumer;
import com.jdddata.datahub.msghub.service.consumer.particle.ConsumerParticle;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RocketMqConsumer
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/18 15:13
 * @modified By:
 */
public class RocketMqConsumer implements IConsumer {

    public static final Logger LOGGER = LoggerFactory.getLogger(RocketMqConsumer.class);

    //缓存 一个key 一个缓存队列。最多存储一万条
//    private final Map<String, BlockingQueue<MessageCache>> STRING_BLOCKING_QUEUE_MAP = new ConcurrentHashMap<>();
//
//    private BlockingQueue<MessageCache> MESSAGE_CACHES = new LinkedBlockingQueue<>();
//
//    private final Map<MessageQueue, Long> OFFSET_TABLE = new ConcurrentHashMap<>();

    //topic和messagetQue的关系，当需要清除的时候需要从OFFSET_TABLE清除
//    private final Map<String, MessageQueue> SET_MAP = new ConcurrentHashMap<>();

//    private final int SIZE_LIMIT = 10000;

    private final ThreadPoolExecutor EXECUTOR_SERVICE;

    private final Map<String, ConsumerParticle> KEY_TOPIC;

    private DefaultMQPullConsumer consumer;

    private String groupName;

    private List<String> topics;

    private String nameSvr;

    //groupname + type Name
    private String key;


    public RocketMqConsumer(String groupName, List<String> topics, String namesvr) throws MQClientException {
        this.KEY_TOPIC = new ConcurrentHashMap<>();
        this.groupName = groupName;
        this.topics = topics;
        this.nameSvr = namesvr;
        this.key = Utils.consumerKey(MsghubConstants.ROCKET_MQ, groupName);
        consumer = new DefaultMQPullConsumer(groupName);
        consumer.setNamesrvAddr(nameSvr);
        consumer.start();
        this.EXECUTOR_SERVICE = new ThreadPoolExecutor(1, 20, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                r -> new Thread(r, "pullMessageFromMQ-" + groupName),
                new ThreadPoolExecutor.AbortPolicy());
        init(groupName, topics);
        Metrics.defaultRegistry().register(MetricRegistry.name(RocketMqConsumer.class, groupName, "running_counting"), new Gauge<Long>() {
            @Override
            public Long getValue() {
                return Long.valueOf(EXECUTOR_SERVICE.getActiveCount());
            }
        });
    }

    private void init(String groupName, List<String> topics) throws MQClientException {
        for (String topic : topics) {
            KEY_TOPIC.put(topic, new ConsumerParticle(consumer, MsghubConstants.ROCKET_MQ, groupName, topic, 1000L, (long) 0));
        }
    }

    @Override
    public void start() {
        KEY_TOPIC.values().forEach(t -> EXECUTOR_SERVICE.submit(t));
    }

    @Override
    public HubPullResult pullMessage(Long offset, Integer max, String topic) {
        ConsumerParticle consumerParticle = KEY_TOPIC.get(topic);
        return consumerParticle.pullMessges(offset, max);
    }

    @Override
    public boolean commitOffset(String topic, Long offset) throws MQClientException {
        ConsumerParticle consumerParticle = KEY_TOPIC.get(topic);
        return consumerParticle.commitOffset(offset);
    }

    @Override
    public void close() throws InterruptedException {
        EXECUTOR_SERVICE.shutdown();
        EXECUTOR_SERVICE.awaitTermination(60,TimeUnit.SECONDS);
        consumer.shutdown();
    }


//    @Override
//    public void start() throws MQClientException {
//        consumer = new DefaultMQPullConsumer(groupName);
//        consumer.setNamesrvAddr(nameSvr);
//        consumer.start();
//        for (String topic : topics) {
//            Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topic);
//            for (MessageQueue mq : mqs) {
//                long l = consumer.fetchConsumeOffset(mq, true);
//                putMessageQueueOffset(mq, l);
//            }
//        }
//    }
//
//    @Override
//    public HubPullResult pullMessage(Long offset, Integer max, String topic) {
//        //TODO 暂时不考虑offset
//        BlockingQueue<MessageCache> messageCaches = STRING_BLOCKING_QUEUE_MAP.get(generateCacheKey(key, topic));
//        if (null == messageCaches) {
//            LOGGER.debug("topic:{} has no messageChache", topic);
//            return new HubPullResult(HubPullStats.NO_MESSAGE, null);
//        }
//        LOGGER.debug("pullMEssage and messageCVahce size is {}", messageCaches.size());
//        int num = (null != max && max < 32) ? max : 32;
//
//        List<MessageCache> messageCacheList = new ArrayList<>(num);
//        List<HubMessageExt> messageExts = new ArrayList<>(num);
//
//        int i = messageCaches.drainTo(messageCacheList, num);
//        if (i <= 0) {
//            LOGGER.debug("topic:{} draint is null", topic);
//            return new HubPullResult(HubPullStats.NO_MESSAGE, null);
//        }
//        for (int j = 0; j < messageCacheList.size(); j++) {
//            messageExts.add(messageCacheList.get(j).getHubMessageExt());
//        }
//        HubPullResult hubPullResult = new HubPullResult(HubPullStats.OK, messageExts);
//        LOGGER.debug(hubPullResult.toString() + "topic is {}", topic);
//        return hubPullResult;
//    }
//
//    @Override
//    public boolean commitOffset(String topic, Long offset) throws MQClientException {
//        MessageQueue messageQueue = SET_MAP.get(generateCacheKey(key, topic));
//        if (null == messageQueue) {
//            return false;
//        }
//        consumer.updateConsumeOffset(messageQueue, offset);
//        return true;
//    }
//
//
//    @Override
//    public void run() {
//        try {
//            for (String topic : topics) {
//                LOGGER.debug("run pullMessage topic:{} start pull ", topic);
//                BlockingQueue<MessageCache> messageCaches = STRING_BLOCKING_QUEUE_MAP.get(generateCacheKey(key, topic));
//                if (null != messageCaches && messageCaches.size() > SIZE_LIMIT) {
//                    break;
//                }
//                Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues(topic);
//                for (MessageQueue mq : mqs) {
//                    try {
//                        PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
//                        putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
//                        putKeyMessageQueues(generateCacheKey(key, topic), mq);
//                        switch (pullResult.getPullStatus()) {
//                            case FOUND:
//                                process(topic, pullResult, messageCaches);
//                                break;
//                            case NO_MATCHED_MSG:
//                                break;
//                            case NO_NEW_MSG:
//                                break;
//                            case OFFSET_ILLEGAL:
//                                break;
//                            default:
//                                break;
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        } catch (
//                MQClientException e) {
//            //TODO 通知
//        }
//    }
//
//    private String generateCacheKey(String key, String topic) {
//        return key + "_" + topic;
//    }
//
//
//    private void putKeyMessageQueues(String key, MessageQueue mq) {
//        SET_MAP.put(key, mq);
//    }
//
//    /**
//     * 处理数据，
//     * 把获取到的数据缓存到 STRING_BLOCKING_QUEUE_MAP
//     *
//     * @param topic
//     * @param pullResult
//     * @param messageCaches
//     */
//    private void process(String topic, PullResult pullResult, BlockingQueue<MessageCache> messageCaches) {
//
//        if (null == messageCaches) {
//            messageCaches = new LinkedBlockingQueue<>();
//        }
//        long maxOffset = pullResult.getMaxOffset();
//        long minOffset = pullResult.getMinOffset();
//        long nextBeginOffset = pullResult.getNextBeginOffset();
//
//        LOGGER.debug("process topic is {} and this size:{}", topic, messageCaches.size());
//
//        for (MessageExt messageExt : pullResult.getMsgFoundList()) {
//            HubMessage hubMessage = JSON.parseObject(messageExt.getBody(), HubMessage.class);
//            messageCaches.offer(new MessageCache(topic, maxOffset, minOffset, nextBeginOffset, hubMessage, messageExt));
//        }
//        STRING_BLOCKING_QUEUE_MAP.put(generateCacheKey(key, topic), messageCaches);
//    }
//
//    private BlockingQueue<MessageCache> fetchMessageCacheQueue(String key) {
//        BlockingQueue<MessageCache> messageCaches = STRING_BLOCKING_QUEUE_MAP.get(key);
//        if (null == messageCaches) {
//            messageCaches = new LinkedBlockingQueue<>();
//        }
//        return messageCaches;
//    }
//
//    /**
//     * 通过Message获取nextOffset
//     *
//     * @param mq
//     * @return
//     */
//    private long getMessageQueueOffset(MessageQueue mq) {
//        Long offset = OFFSET_TABLE.get(mq);
//        if (offset != null) {
//            return offset;
//        }
//        return 0;
//    }
//
//
//    /**
//     * 把nextoffset缓存起来
//     *
//     * @param mq
//     * @param offset
//     */
//    private void putMessageQueueOffset(MessageQueue mq, long offset) {
//        OFFSET_TABLE.put(mq, offset);
//    }
}