package com.kingcent.campus.shop.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;

/**
 * @author rainkyzhong
 * @date 2023/8/8 9:34
 */
//@Configuration
public class RedissonConfig {


    @Bean
    public RedissonClient redisson(){
        return Redisson.create();
    }
}
