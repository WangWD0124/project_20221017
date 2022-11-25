package com.wwd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.wwd.modules.ware.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class ProjectWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectWareApplication.class, args);
    }
}