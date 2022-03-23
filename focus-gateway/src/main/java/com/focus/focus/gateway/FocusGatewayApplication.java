package com.focus.focus.gateway;

import com.focus.focus.gateway.model.SysParameterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@EnableConfigurationProperties(value = {SysParameterConfig.class})
public class FocusGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FocusGatewayApplication.class, args);
    }

}
