package com.jdddata.datahub.msghub.service.api;

import com.jdddata.datahub.common.service.message.HubMessage;
import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.MsgHubContext;

/**
 * @InterfaceName: ProducerServiceApi
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 9:58
 * @modified By:
 */
public interface ProducerServiceApi {
    boolean send(HubMessage message);

    boolean isStartable();

    void close();

    void setStartable(boolean b);

    void initProducer(MsgHubContext msgHubContext) throws RocketMQException;
}
