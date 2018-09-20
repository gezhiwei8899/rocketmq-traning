package com.jdddata.datahub.msghub.service.consumer.job;

import com.jdddata.datahub.msghub.common.IdletimeCloseConsumerException;
import com.jdddata.datahub.msghub.service.consumer.cache.Connection;
import com.jdddata.datahub.msghub.service.consumer.cache.ConnectionCache;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName: IdleTimeService
 * @Author: 葛志伟(赛事)
 * @Description:
 * @Date: 2018/9/20 15:47
 * @modified By:
 */
@Component
public class IdleTimeService {
    public static final Logger LOGGER = LoggerFactory.getLogger(IdleTimeService.class);

    @Scheduled(cron = "0 0/5 * * * *")
    public void idleTimeClose() {
        for (Connection connection : ConnectionCache.values()) {
            Date date = DateUtils.addMinutes(connection.getIdleTime(), 15);
            int i = DateUtils.truncatedCompareTo(new Date(), date, Calendar.MINUTE);
            if (i > 1) {
                try {
                    connection.close();
                } catch (InterruptedException | IdletimeCloseConsumerException e) {
                    LOGGER.error("close connection:{}-{} failed", connection.getUuid(), connection.getGroupName());
                }
            }
        }
    }
}
