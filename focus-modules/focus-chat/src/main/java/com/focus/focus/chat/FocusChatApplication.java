package com.focus.focus.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import java.util.TimeZone;


@EnableFeignClients(basePackages = "com.focus.focus.api")
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.focus.focus.chat","com.focus.redis.common"})
public class FocusChatApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(FocusChatApplication.class, args);
    }

}
