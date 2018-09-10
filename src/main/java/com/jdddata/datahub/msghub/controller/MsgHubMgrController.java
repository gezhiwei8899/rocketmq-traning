package com.jdddata.datahub.msghub.controller;

import com.jdddata.datahub.msghub.common.RocketMQException;
import com.jdddata.datahub.msghub.config.MQInfo;
import com.jdddata.datahub.msghub.config.MsgHubConfig;
import com.jdddata.datahub.msghub.producer.ProducerBase;
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

    @GetMapping("/start")
    public String start() {
        try {
            if (!ProducerBase.INSTANCE.isStartable()) {


                MsgHubConfig msgHubConfig = new MsgHubConfig();
                List<MQInfo> mqInfos = new ArrayList<>();
                MQInfo v = new MQInfo();
                v.setMqName(environment.getProperty("rocketmq.name"));
                v.setConnect(environment.getProperty("rocketmq.url"));
                mqInfos.add(v);
                msgHubConfig.setMqInfoList(mqInfos);

                ProducerBase.INSTANCE.init(msgHubConfig);

                ProducerBase.INSTANCE.setStartable(true);
                return "ok";
            } else {
                return "已经启动";
            }
        } catch (RocketMQException e) {
            return "启动失败";
        }
    }
}
