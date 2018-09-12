package com.jdddata.datahub.msghub.service.api;

import com.jdddata.datahub.common.service.message.Message;
import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.MsgHubConfig;

/**
 * @InterfaceName: ProducerServiceApi
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/12 9:58
 * @modified By:
 */
public interface ProducerServiceApi {
    boolean send(String namespace, String schema, Message message);

    boolean isStartable();

    void close();

    void init(MsgHubConfig msgHubConfig) throws RocketMQException;

    void setStartable(boolean b);
}
