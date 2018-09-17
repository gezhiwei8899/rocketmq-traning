//package com.jdddata.datahub.msghub.service.consumer.job;
//
//import com.jdddata.datahub.msghub.service.consumer.connection.Connection;
//import com.jdddata.datahub.msghub.service.consumer.connection.ConnectionCache;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.Map;
//
///**
// * @ClassName: IdleTimeService
// * @Author: 葛志伟(赛事)
// * @Description:
// * @Date: 2018/9/14 13:38
// * @modified By:
// */
//@Component
//public class IdleTimeService {
//
//    @Scheduled(cron = "0 0/5 * * * *")
//    public void idleTime(){
//        Map<String, Connection> map = ConnectionCache.listAll();
//        for (Map.Entry<String, Connection> connectionEntry : map.entrySet()) {
//            Date time = connectionEntry.getValue().getIdleTime();
//
//
//        }
//    }
//}
