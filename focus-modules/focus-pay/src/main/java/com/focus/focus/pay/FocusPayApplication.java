package com.focus.focus.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.TimeZone;

@EnableFeignClients(basePackages = "com.focus.focus.api")
@EnableDiscoveryClient
@SpringBootApplication
public class FocusPayApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(FocusPayApplication.class, args);
    }

}
