package com.kingcent.gateway.service;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.common.entity.constant.LoginType;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 鉴权服务
 * @author rainkyzhong
 * @date 2023/06/15
 */
@Service
public class AuthService {

    @Resource
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    public Long check(Long lid, String sign, String path, String data, LoginType loginType){
        Long[] success = {null};
        try {
            //开启新线程，防止阻塞
            Thread t = new Thread(()->{
                ServiceInstance instance = loadBalancerClient.choose("kingcent-auth");
                if(instance == null){
                    return;
                }
                String url = String.format("http://%s:%s/login/check", instance.getHost(), instance.getPort());
                JSONObject info = new JSONObject();
                info.put("lid", lid);
                info.put("sign", sign);
                info.put("path",path);
                info.put("data", data);
                info.put("login-type", loginType);
                success[0] = restTemplate.postForObject(url, info, Long.class);
            });
            t.start();
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(success[0] == null)
            throw new RuntimeException("无法连接到鉴权服务器");
        return success[0];
    }
}
