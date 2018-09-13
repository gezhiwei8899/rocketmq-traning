package com.jdddata.datahub.msghub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: MsgHubConfiguration
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/13 11:19
 * @modified By:
 */
@Configuration
public class MsgHubConfiguration {

    @Bean(name = "rocketmqContext")
    @ConfigurationProperties(prefix = "messagehub.rocketmq")
    public RocketMqContext getRocketmqContext() {
        return new RocketMqContext();
    }
}
