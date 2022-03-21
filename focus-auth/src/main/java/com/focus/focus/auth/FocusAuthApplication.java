package com.focus.focus.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@EnableDiscoveryClient
@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.focus.auth.common","com.focus.focus.auth"})
public class FocusAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusAuthApplication.class, args);
    }

}
