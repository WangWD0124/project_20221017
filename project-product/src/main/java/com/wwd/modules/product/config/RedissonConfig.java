package com.wwd.modules.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonConfig {

    //所有对Redisson的使用都是通过Redissonclient对象
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson()throws IOException {
        //1、创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("192.168.56.10:6379");
        //2、根据Config创建出Redissonclient示例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}