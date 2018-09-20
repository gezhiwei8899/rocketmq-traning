package com.jdddata.datahub.msghub.service.consumer.cache;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: Connection
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/18 14:55
 * @modified By:
 */
public class Connection {

    private String instanceId;

    private String uuid;

    private String type;

    private String groupName;

    private List<String> topics;

    private Date idleTime;

    public Connection(String uuid, String type, String groupName, String instanceId, List<String> topics) {
        this.uuid = uuid;
        this.type = type;
        this.groupName = groupName;
        this.instanceId = instanceId;
        this.topics = topics;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Date getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(Date idleTime) {
        this.idleTime = idleTime;
    }

    public void add(Connection connection) {

    }

    public void refresh() {
        setIdleTime(new Date());
    }
}
