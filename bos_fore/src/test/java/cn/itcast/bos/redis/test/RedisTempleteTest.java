package cn.itcast.bos.redis.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class RedisTempleteTest {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Test
    public void testRedis(){
        //30秒失效
        redisTemplate.opsForValue().set("username","zhangsan",30, TimeUnit.SECONDS);
        System.out.println(redisTemplate.opsForValue().get("username"));
    }
}
