package com.kingcent.auth.servcice.impl;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.auth.entity.UserLoginEntity;
import com.kingcent.auth.mapper.UserLoginMapper;
import com.kingcent.auth.servcice.UserLoginService;
import com.kingcent.common.entity.constant.LoginType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLoginEntity> implements UserLoginService {
    private final static String PREFIX_OF_CUSTOMER_LOGIN_KEY = "customer_login_info";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Long check(JSONObject object) {
        String sign = object.getString("sign");
        String path = object.getString("path");
        String data = object.getString("data");
        LoginType loginType = object.getObject("login-type", LoginType.class);
        Long lid = object.getLong("lid");

        if(loginType == null) return -1L;

        switch (loginType){
            case ADMIN -> {
                UserLoginEntity login = getById(lid);
                //TODO 鉴权
                return login.getId();
            }
            case CUSTOMER -> {

                //读取redis中的secret
                ValueOperations<String, String> ops = redisTemplate.opsForValue();
                String cache = ops.get(PREFIX_OF_CUSTOMER_LOGIN_KEY+"_"+lid);
                String secret;
                Long uid;
                if (cache == null){
                    //读取数据库中的secret
                    UserLoginEntity userLoginEntity = getById(lid);
                    if (userLoginEntity == null){
                        return -1L;
                    }
                    secret = userLoginEntity.getSecret();
                    uid = userLoginEntity.getUserId();
                    //写入redis缓存
                    ops.set(PREFIX_OF_CUSTOMER_LOGIN_KEY+"_"+lid, uid+","+secret, 1, TimeUnit.HOURS);
                }else{
                    String[] info = cache.split(",");
                    uid = Long.valueOf(info[0]);
                    secret = info[1];
                }

                long timestamp = System.currentTimeMillis()/60000;
                for (int i = -1; i < 1; i++){
                    if (sign.equals(getSign(uid,secret,path,timestamp+i,data)))
                        return uid;
                }

                return -1L;
            }
            default -> {
                return -1L;
            }
        }
    }

    /**
     * 计算签名
     */
    private String getSign(Long userId, String secret, String path, long timestamp, String data){
        String value = "uid="+userId+"&secret="+secret+"&path="+path+"&timestamp="+timestamp+"/"+data;
        String sign = MD5.create().digestHex(value.getBytes());
        log.info("计算签名：\n{}-->{}", value,sign);
        return sign;
    }
}
