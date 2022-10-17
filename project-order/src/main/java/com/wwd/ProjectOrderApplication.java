package com.wwd;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProjectOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectOrderApplication.class, args);
    }
}
