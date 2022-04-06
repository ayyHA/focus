package com.focus.focus.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "com.focus.focus.api")
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.focus.focus.message","com.focus.auth.common.security.filter","com.focus.focus.api.oss"})
public class FocusMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusMessageApplication.class, args);
    }

}
