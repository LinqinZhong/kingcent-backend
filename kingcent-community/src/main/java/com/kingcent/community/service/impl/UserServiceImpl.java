package com.kingcent.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.community.service.UserService;
import com.kingcent.common.user.entity.UserInfoEntity;
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

/**
 * @author rainkyzhong
 * @date 2023/12/12 15:56
 */
@Service
public class UserServiceImpl implements UserService {

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
                ServiceInstance instance = loadBalancerClient.choose("kingcent-user");
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
}
