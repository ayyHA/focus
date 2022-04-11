package com.focus.focus.message.job;

import com.focus.focus.message.service.ILikeService;
import com.focus.focus.message.service.IPublicDataService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.util.Date;

// 点赞的定时作业
@Slf4j
public class LikeJob extends QuartzJobBean {
    @Autowired
    private ILikeService likeService;
    @Autowired
    private IPublicDataService publicDataService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("LikeJob Now {} is running!",sdf.format(new Date()));
        // 同步Redis的数据至MySQL中
        likeService.transLikeFromRedisToDB();
        publicDataService.transLikeCountFromRedisToDB();
        // 将messagePublicData数据重新装载?
    }
}
