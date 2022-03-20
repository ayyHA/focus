package com.focus.focus.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FocusChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusChatApplication.class, args);
    }

}
