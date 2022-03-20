package com.focus.focus.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FocusPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusPayApplication.class, args);
    }

}
