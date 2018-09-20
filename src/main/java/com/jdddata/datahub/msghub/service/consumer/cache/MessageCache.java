package com.jdddata.datahub.msghub.service.consumer.cache;


import com.jdddata.datahub.common.service.consumer.HubMessageExt;
import com.jdddata.datahub.common.service.message.HubMessage;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @ClassName: MessageCache
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 11:01
 * @modified By:
 */
public final class MessageCache {

    private final String topic;
    private final long nextBeginOffset;
    private final long minOffset;
    private final long maxOffset;
    private HubMessageExt hubMessageExt;

    public MessageCache(String topic, long maxOffset, long minOffset, long nextBeginOffset, HubMessage hubMessage, MessageExt messageExt) {
        this.topic = topic;
        this.maxOffset = maxOffset;
        this.minOffset = minOffset;
        this.nextBeginOffset = nextBeginOffset;
        this.hubMessageExt = convert(messageExt, hubMessage);

    }

    private HubMessageExt convert(MessageExt messageExt, HubMessage hubMessage) {
        HubMessageExt hubMessageExt = new HubMessageExt();
        hubMessageExt.setHubMessage(hubMessage);
        hubMessageExt.setQueueId(messageExt.getQueueId());
        hubMessageExt.setQueueOffset(messageExt.getQueueOffset());
        hubMessageExt.setTopic(topic);
        return hubMessageExt;
    }


    public String getTopic() {
        return topic;
    }

    public long getNextBeginOffset() {
        return nextBeginOffset;
    }

    public long getMinOffset() {
        return minOffset;
    }

    public long getMaxOffset() {
        return maxOffset;
    }

    public HubMessageExt getHubMessageExt() {
        return hubMessageExt;
    }

    public void setHubMessageExt(HubMessageExt hubMessageExt) {
        this.hubMessageExt = hubMessageExt;
    }
}