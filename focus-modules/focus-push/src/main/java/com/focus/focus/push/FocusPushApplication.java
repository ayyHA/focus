package com.focus.focus.push;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FocusPushApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusPushApplication.class, args);
    }

}
