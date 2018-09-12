package com.jdddata.datahub.msghub.service.api;


import com.jdddata.datahub.common.service.message.Message;

/**
 * @InterfaceName: ProducerDataHandler
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 9:51
 * @modified By:
 */
public interface ProducerDataHandler {

    boolean store(Message message);

    Message take() throws InterruptedException;

}
