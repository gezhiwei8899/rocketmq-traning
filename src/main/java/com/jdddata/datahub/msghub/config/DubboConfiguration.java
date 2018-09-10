package com.jdddata.datahub.msghub.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: DubboConfiguration
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/7 16:40
 * @modified By:
 */
@Configuration
public class DubboConfiguration {
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("provider-test");
        return applicationConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("zookeeper://192.168.136.117:2181");
        registryConfig.setClient("curator");
        return registryConfig;
    }
}
