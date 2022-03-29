package com.focus.focus.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.focus.auth.common.security.filter","com.focus.focus.user"})
public class FocusUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusUserApplication.class, args);
    }

}
