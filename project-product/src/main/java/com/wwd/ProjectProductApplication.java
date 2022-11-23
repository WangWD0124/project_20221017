package com.wwd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.wwd.modules.product.feign")
@EnableDiscoveryClient //开启nacos服务注册
@SpringBootApplication
public class ProjectProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectProductApplication.class, args);
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ProjectProductApplication.class);
    }
}
