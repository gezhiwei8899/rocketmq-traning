package com.gezhiwei.msghub;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @ClassName: RocketMqTest
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/18 14:35
 * @modified By:
 */
public class RocketMqTest {

    private DefaultMQProducer defaultMQProducer;

    private String gourpName = "topic";

    @Test
    public void test() {
        String[] topic = {"default_ke_ke_metrics", "default_ke_ke_zk"};
        for (String s : topic) {
            defaultMQProducer = new DefaultMQProducer(UUID.randomUUID().toString());
            defaultMQProducer.setNamesrvAddr("192.168.136.158:9876");
            try {
                defaultMQProducer.start();
            } catch (MQClientException e) {
                e.printStackTrace();
                System.out.println("asdfasdf");
            }
        }
    }

    @Test
    public void tee() {
        System.out.println(new Date());
        Date date = new Date();
        Date date1 = DateUtils.addMinutes(date, 5);
        int i = DateUtils.truncatedCompareTo(date1, date1, Calendar.MINUTE);
        System.out.println(i);
    }
}
