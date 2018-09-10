package com.jdddata.datahub.msghub;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@DubboComponentScan(basePackages = "com.jdddata.datahub.msghub")
public class DatahubServer {
    public static void main(String[] args) {
        SpringApplication.run(DatahubServer.class, args);
    }
}
