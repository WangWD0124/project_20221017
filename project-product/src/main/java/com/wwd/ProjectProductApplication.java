package com.wwd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProjectProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectProductApplication.class, args);
    }
}
