package com.jdddata.datahub.msghub.service.consumer.connection;

import java.util.Date;

/**
 * @ClassName: Connection
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/14 13:59
 * @modified By:
 */
public class Connection {

    //连接唯一标识
    private String uuid;

    //连接指针，指向具体的consumers
    private String ref;

    private Date idleTime;

    public Connection(String uuid, String toString, Date date) {
        this.uuid = uuid;
        this.ref = toString;
        this.idleTime = date;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Date getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(Date idleTime) {
        this.idleTime = idleTime;
    }

    public void refresh() {
        setIdleTime(new Date());
    }
}
