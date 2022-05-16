package com.focus.focus.message.config;

import com.focus.focus.message.job.LikeJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {
    private static final String LIKE_JOB_IDENTITY = "LikeJob";

    @Bean
    public JobDetail likeJobDetail(){
        return JobBuilder.newJob(LikeJob.class)
                .withIdentity(LIKE_JOB_IDENTITY)
                // 无trigger关联亦无需删除此JobDetail
                .storeDurably().build();
    }

    @Bean
    public Trigger likeJobTrigger(){
        SimpleScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(2)      // Schedule容器 2minutes Trigger一次JobDetail里的Job
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(likeJobDetail())
                .withIdentity(LIKE_JOB_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }
}
