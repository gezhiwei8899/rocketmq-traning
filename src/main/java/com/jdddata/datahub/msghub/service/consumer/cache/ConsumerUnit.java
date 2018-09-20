package com.jdddata.datahub.msghub.service.consumer.cache;

import com.jdddata.datahub.msghub.service.api.IConsumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: ConsumerUnit
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/14 14:05
 * @modified By:
 */
public class ConsumerUnit {

    private String key;

    private IConsumer iConsumer;

    private Map<Connection, String> connections;

    public ConsumerUnit(String key, IConsumer iConsumer, Connection connection) {
        this.key = key;
        this.iConsumer = iConsumer;
        this.connections = new ConcurrentHashMap<>();
        this.connections.put(connection, connection.getUuid());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public IConsumer getiConsumer() {
        return iConsumer;
    }

    public void setiConsumer(IConsumer iConsumer) {
        this.iConsumer = iConsumer;
    }

    public Map<Connection, String> getConnections() {
        return connections;
    }

    public void setConnections(Map<Connection, String> connections) {
        this.connections = connections;
    }

    public void add(Connection connection) {
        this.connections.put(connection, connection.getUuid());
    }
}
