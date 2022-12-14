package com.wwd.projectproduct;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class ProjectProductApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Test
    public void testStringRedisTemplate() {
        stringRedisTemplate.opsForValue().set("h", "hh");
        System.out.println(stringRedisTemplate.opsForValue().get("h"));
    }




}
