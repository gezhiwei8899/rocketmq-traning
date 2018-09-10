package com.jdddata.datahub.msghub.config;

/**
 * @ClassName: MQInfo
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 10:51
 * @modified By:
 */
public class MQInfo {

    /**
     * kafka or rocketmq
     */
    private String mqName;

    private String connect;

    private String groupName;
    private String namesrvAddr;
    private String instanceName;
    private int maxMessageSize;
    private int sendMsgTimeout;

    public String getMqName() {
        return mqName;
    }

    public void setMqName(String mqName) {
        this.mqName = mqName;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public int getSendMsgTimeout() {
        return sendMsgTimeout;
    }

    public void setSendMsgTimeout(int sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }
}
