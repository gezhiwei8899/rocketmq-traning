package com.jdddata.datahub.msghub.config;

/**
 * @ClassName: MsgHubContext
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/13 11:05
 * @modified By:
 */
public class MsgHubContext {

    private String namesvr;

    private String rocketGroupName;

    private String kafkaBroker;

    private String kafkaGroupName;

    public String getNamesvr() {
        return namesvr;
    }

    public void setNamesvr(String namesvr) {
        this.namesvr = namesvr;
    }

    public String getRocketGroupName() {
        return rocketGroupName;
    }

    public void setRocketGroupName(String rocketGroupName) {
        this.rocketGroupName = rocketGroupName;
    }

    public String getKafkaBroker() {
        return kafkaBroker;
    }

    public void setKafkaBroker(String kafkaBroker) {
        this.kafkaBroker = kafkaBroker;
    }

    public String getKafkaGroupName() {
        return kafkaGroupName;
    }

    public void setKafkaGroupName(String kafkaGroupName) {
        this.kafkaGroupName = kafkaGroupName;
    }
}
