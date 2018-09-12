package com.jdddata.datahub.msghub.controller;

import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.MQInfo;
import com.jdddata.datahub.msghub.config.MsgHubConfig;
import com.jdddata.datahub.msghub.service.api.ProducerServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MsgHubMgrController
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/10 14:25
 * @modified By:
 */
@RestController()
@RequestMapping("/msghub")
public class MsgHubMgrController {

    @Autowired
    private Environment environment;

    @Autowired
    private ProducerServiceApi producerServiceApi;

    @GetMapping("/start")
    public String start() {
        try {
            if (!producerServiceApi.isStartable()) {


                MsgHubConfig msgHubConfig = new MsgHubConfig();
                List<MQInfo> mqInfos = new ArrayList<>();
                MQInfo v = new MQInfo();
                v.setGroupName("sadf");
                v.setNamesrvAddr("192.168.136.158:9876");
                v.setInstanceName("asdf");
                v.setMaxMessageSize(10000);
                v.setSendMsgTimeout(1000000);
                v.setMqName(environment.getProperty("rocketmq.name"));
                v.setConnect(environment.getProperty("rocketmq.url"));

                mqInfos.add(v);
                msgHubConfig.setMqInfoList(mqInfos);

                producerServiceApi.init(msgHubConfig);

                producerServiceApi.setStartable(true);
                return "ok";
            } else {
                return "已经启动";
            }
        } catch (RocketMQException e) {
            return "启动失败";
        }
    }
}
