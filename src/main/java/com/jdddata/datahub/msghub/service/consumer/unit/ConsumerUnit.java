package com.jdddata.datahub.msghub.service.consumer.unit;

import com.jdddata.datahub.msghub.service.api.IConsumer;

import java.util.ArrayList;
import java.util.List;

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

    private List<String> bindings;

    public ConsumerUnit(String s, IConsumer iConsumer, String uuid) {
        this.key=s;
        this.iConsumer = iConsumer;
        init(uuid);
    }

    public ConsumerUnit() {

    }

    private void init(String uuid) {
        List<String> bindings = getBindings();
        if (null == bindings) {
            bindings = new ArrayList<>();
        }
        bindings.add(uuid);
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

    public List<String> getBindings() {
        return bindings;
    }

    public void setBindings(List<String> bindings) {
        this.bindings = bindings;
    }
}
