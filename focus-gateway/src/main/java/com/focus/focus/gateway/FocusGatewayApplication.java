package com.focus.focus.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FocusGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusGatewayApplication.class, args);
    }

}
