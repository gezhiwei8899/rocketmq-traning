package com.jdddata.datahub.msghub.service.consumer.factory;

import com.jdddata.datahub.msghub.common.ConsumerRegisterException;
import com.jdddata.datahub.msghub.common.MsghubConstants;
import com.jdddata.datahub.msghub.config.MsgHubContext;
import com.jdddata.datahub.msghub.service.api.IConsumer;
import com.jdddata.datahub.msghub.service.consumer.rocketmq.RocketMqConsumer;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: ConsumerFactory
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 11:38
 * @modified By:
 */
public class ConsumerFactory {
    //TODO 魔鬼文字
    public static IConsumer createInstance(String type, String groupName, String topic, MsgHubContext msgHubContext) throws ConsumerRegisterException {

        if (null == type || StringUtils.equalsIgnoreCase(MsghubConstants.ROCKET_MQ, type)) {
            if (null == msgHubContext.getNamesvr()) {
                throw new ConsumerRegisterException("不存在rocketmq");
            }
            return new RocketMqConsumer(groupName, topic, msgHubContext);
        }

        return null;
    }
}
