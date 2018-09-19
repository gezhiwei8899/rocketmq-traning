package com.jdddata.datahub.msghub.service.consumer;

import com.jdddata.datahub.common.service.consumer.HubPullResult;
import com.jdddata.datahub.common.service.consumer.HubPullStats;
import com.jdddata.datahub.msghub.common.ConsumerRegisterException;
import com.jdddata.datahub.msghub.common.MsghubConstants;
import com.jdddata.datahub.msghub.common.Utils;
import com.jdddata.datahub.msghub.config.MsgHubContext;
import com.jdddata.datahub.msghub.service.api.ConsumerServiceApi;
import com.jdddata.datahub.msghub.service.api.IConsumer;
import com.jdddata.datahub.msghub.service.consumer.cache.ConsumerCache;
import com.jdddata.datahub.msghub.service.consumer.rocketmq.RocketMqConsumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ConsumerBase
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/18 15:12
 * @modified By:
 */

@Service
public class ConsumerBase implements ConsumerServiceApi {

    @Autowired
    private MsgHubContext msgHubContext;

    @Override
    public boolean updateOffset(String type, String groupName, String topic, Long offset) {
        return false;
    }

    @Override
    public void register(String type, String groupName, List<String> topics) throws ConsumerRegisterException, MQClientException {

        if (StringUtils.isNotBlank(type)) {
            if (StringUtils.equalsIgnoreCase(MsghubConstants.ROCKET_MQ, type)) {
                if (null == msgHubContext.getNamesvr()) {
                    throw new ConsumerRegisterException("namesvr is null");
                }
            }
        } else {
            throw new ConsumerRegisterException("type is null");
        }

        if (null == type || StringUtils.equalsIgnoreCase(MsghubConstants.ROCKET_MQ, type)) {
            IConsumer iConsumer = new RocketMqConsumer(groupName, topics, msgHubContext.getNamesvr());
            iConsumer.start();
            ConsumerCache.cacheConsumer(type, groupName, iConsumer);
        }
    }

    @Override
    public HubPullResult pullConsumer(String type, String groupName, String uuid, String topic, Long offset, Integer max) {
        String key = Utils.consumerKey(type, groupName);
        IConsumer iConsumer = ConsumerCache.getConsumerCache(key);
        if (null == iConsumer) {
            return new HubPullResult(HubPullStats.NO_MESSAGE, null);
        }
        return iConsumer.pullMessage(offset, max);
    }
}
