package com.jdddata.datahub.msghub.config;

/**
 * @ClassName: RocketMqContext
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/13 11:05
 * @modified By:
 */
public class RocketMqContext {
    private String namesvr;

    private String producerGroupname;

    public String getNamesvr() {
        return namesvr;
    }

    public void setNamesvr(String namesvr) {
        this.namesvr = namesvr;
    }

    public String getProducerGroupname() {
        return producerGroupname;
    }

    public void setProducerGroupname(String producerGroupname) {
        this.producerGroupname = producerGroupname;
    }
}
