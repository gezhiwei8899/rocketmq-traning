package com.jdddata.datahub.msghub.message;

import com.jdddata.datahub.common.service.Message;

/**
 * @InterfaceName: IReceive
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 9:51
 * @modified By:
 */
public interface IReceive {

    boolean store(Message message);

    Message take() throws InterruptedException;

}
