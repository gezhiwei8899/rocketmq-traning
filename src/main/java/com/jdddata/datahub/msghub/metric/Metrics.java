package com.jdddata.datahub.msghub.metric;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;

/**
 * @ClassName: Metrics
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 9:33
 * @modified By:
 */
public final class Metrics {
    private static final MetricRegistry DEFAULT_REGISTRY = new MetricRegistry();

    static {
        JmxReporter jmxReporter = JmxReporter.forRegistry(DEFAULT_REGISTRY).build();
        jmxReporter.start();
    }

    public static MetricRegistry defaultRegistry() {
        return DEFAULT_REGISTRY;
    }
}
