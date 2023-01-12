package com.wwd.modules.member.config;

//@Configuration
//public class RedissonConfig {
//
//    //所有对Redisson的使用都是通过Redissonclient对象
//    @Bean(destroyMethod="shutdown")
//    public RedissonClient redisson()throws IOException {
//        //1、创建配置
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://192.168.145.101:6379");
//        //2、根据Config创建出Redissonclient示例
//        RedissonClient redissonClient = Redisson.create(config);
//        return redissonClient;
//    }
//}
