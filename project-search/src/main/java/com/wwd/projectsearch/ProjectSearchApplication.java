package com.wwd.projectsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ProjectSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectSearchApplication.class, args);
    }

}
