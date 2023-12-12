package com.kingcent.campus.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.user.entity.UserInfoEntity;
import com.kingcent.campus.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class AppUserInfoService implements UserInfoService {

    @Resource
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map<Long, UserInfoEntity> userInfoMap(Set<Long> userIds) {

        Map<Long, UserInfoEntity> res = new HashMap<>();
        try {
            //开启新线程，防止阻塞
            Thread t = new Thread(()->{
                ServiceInstance instance = loadBalancerClient.choose("kingcent-campus-user");
                if(instance == null){
                    return;
                }
                String url = String.format("http://%s:%s/user_info/heads", instance.getHost(), instance.getPort());
                String result = restTemplate.postForObject(url, userIds, String.class);
                try{
                    JSONObject obj = JSONObject.parseObject(result);
                    if(obj == null || obj.getBoolean("success") == null || !obj.getBoolean("success")) return;
                    for (Object datum : obj.getObject("data", List.class)) {
                        JSONObject info = (JSONObject) datum;
                        UserInfoEntity userInfo = new UserInfoEntity();
                        userInfo.setUserId(info.getLong("userId"));
                        userInfo.setNickname(info.getString("nickname"));
                        userInfo.setGender(info.getInteger("gender"));
                        userInfo.setAvatarUrl(info.getString("avatarUrl"));
                        res.put(userInfo.getUserId(), userInfo);
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            });
            t.start();
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public UserInfoEntity get(Long userId) {
        AtomicReference<UserInfoEntity> result = new AtomicReference<>();
        try {
            //开启新线程，防止阻塞
            Thread t = new Thread(()->{
                ServiceInstance instance = loadBalancerClient.choose("kingcent-campus-user");
                if(instance == null){
                    return;
                }
                String url = String.format("http://%s:%s/user_info/get/%d", instance.getHost(), instance.getPort(), userId);
                result.set(restTemplate.getForObject(url, UserInfoEntity.class));
            });
            t.start();
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result.get();
    }
}
