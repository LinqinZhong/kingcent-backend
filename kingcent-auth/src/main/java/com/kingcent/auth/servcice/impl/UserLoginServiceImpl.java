package com.kingcent.auth.servcice.impl;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.auth.config.LoginConfig;
import com.kingcent.auth.mapper.UserLoginMapper;
import com.kingcent.auth.servcice.UserLoginService;
import com.kingcent.auth.utils.DESUtil;
import com.kingcent.common.user.entity.UserLoginEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLoginEntity> implements UserLoginService {
    private final static String PREFIX_OF_CUSTOMER_LOGIN_KEY = "customer_login_info";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LoginConfig loginConfig;

    @Override
    public Long check(String token) {
        try{
            String decrypt = DESUtil.decrypt(loginConfig.TOKEN_KEY, token);
            JSONObject tokenObj = JSONObject.parseObject(decrypt);
            Long lid = tokenObj.getLong("lid");
            String secret = tokenObj.getString("secret");
            String type = tokenObj.getString("type");
            Long uid = tokenObj.getLong("uid");
            Long expired = tokenObj.getLong("expired");
            if(expired < System.currentTimeMillis()) return -1L;
            if(type.equals("password")){
                UserLoginEntity loginEntity = getById(lid);
                if(
                        !loginEntity.getUserId().equals(uid)
                                && !loginEntity.getSecret().equals(secret)
                ) return -1L;
                return uid;
            }
        }catch (Exception e){
            return -1L;
        }
        return -1L;
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
