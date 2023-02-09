package com.wwd;


import io.seata.config.springcloud.EnableSeataSpringConfig;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableSeataSpringConfig
@EnableRedisHttpSession
@EnableRabbit
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ProjectOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectOrderApplication.class, args);
    }
}
