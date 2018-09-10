package com.jdddata.datahub.msghub.config;

import java.util.List;

/**
 * @ClassName: MsgHubConfig
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 10:14
 * @modified By:
 */
public class MsgHubConfig {

    private Integer messageQueSize;

    private List<MQInfo> mqInfoList;

    public Integer getMessageQueSize() {
        return messageQueSize;
    }

    public void setMessageQueSize(Integer messageQueSize) {
        this.messageQueSize = messageQueSize;
    }

    public List<MQInfo> getMqInfoList() {
        return mqInfoList;
    }

    public void setMqInfoList(List<MQInfo> mqInfoList) {
        this.mqInfoList = mqInfoList;
    }

}
