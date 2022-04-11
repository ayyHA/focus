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
                .storeDurably().build();
    }

    @Bean
    public Trigger likeJobTrigger(){
        SimpleScheduleBuilder scheduleBuilder =
                SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(10)      // Schedule容器10minsTrigger一次JobDetail里的Job
                .repeatForever();
        return TriggerBuilder.newTrigger()
                .forJob(likeJobDetail())
                .withIdentity(LIKE_JOB_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }
}
