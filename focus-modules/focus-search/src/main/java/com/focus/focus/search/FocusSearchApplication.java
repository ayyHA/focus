package com.focus.focus.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.focus.focus.api")
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.focus.focus.search","com.focus.auth.common.security.filter"})
public class FocusSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(FocusSearchApplication.class, args);
    }
}
