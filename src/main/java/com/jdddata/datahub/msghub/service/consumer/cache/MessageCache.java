package com.jdddata.datahub.msghub.service.consumer.cache;


import com.jdddata.datahub.common.service.message.Message;

/**
 * @ClassName: MessageCache
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 11:01
 * @modified By:
 */
public final class MessageCache {

    private String topic;
    private long nextBeginOffset;
    private Message message;
    private long minOffset;
    private long maxOffset;

    public MessageCache(String topic, long maxOffset, long minOffset, long l, Message message) {
        this.topic = topic;
        this.nextBeginOffset = l;
        this.message = message;
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
    }

    public long getMinOffset() {
        return minOffset;
    }

    public void setMinOffset(long minOffset) {
        this.minOffset = minOffset;
    }

    public long getMaxOffset() {
        return maxOffset;
    }

    public void setMaxOffset(long maxOffset) {
        this.maxOffset = maxOffset;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getNextBeginOffset() {
        return nextBeginOffset;
    }

    public void setNextBeginOffset(long nextBeginOffset) {
        this.nextBeginOffset = nextBeginOffset;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
