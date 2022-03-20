package com.focus.focus.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FocusMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusMessageApplication.class, args);
    }

}
