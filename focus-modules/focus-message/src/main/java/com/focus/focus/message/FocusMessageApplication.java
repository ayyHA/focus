package com.focus.focus.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@EnableCaching
@EnableFeignClients(basePackages = "com.focus.focus.api")
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.focus.focus.message","com.focus.auth.common.security.filter","com.focus.focus.api.oss","com.focus.redis.common"})
public class FocusMessageApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(FocusMessageApplication.class, args);
    }

}
