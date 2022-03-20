package com.focus.focus.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FocusUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusUserApplication.class, args);
    }

}
