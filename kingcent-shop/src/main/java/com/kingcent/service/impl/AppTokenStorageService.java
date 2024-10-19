package com.kingcent.service.impl;

import com.kingcent.common.wx.service.WxTokenStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author rainkyzhong
 * @date 2023/8/26 23:19
 */
@Service
public class AppTokenStorageService implements WxTokenStorageService {

    @Autowired
    private StringRedisTemplate  redisTemplate;

    private static final String WX_ACCESS_TOKEN_KEY = "wx:access:token";

    @Override
    public String getWxToken(String appId) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(WX_ACCESS_TOKEN_KEY+":"+appId);
    }

    @Override
    public void setWxToken(String appId, String token) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(WX_ACCESS_TOKEN_KEY+":"+appId, token, 2, TimeUnit.HOURS);
    }
}
