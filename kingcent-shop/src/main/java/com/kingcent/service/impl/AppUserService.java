package com.kingcent.service.impl;

import com.kingcent.common.wx.service.WxUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 用户服务
 * @author rainkyzhong
 * @date 2023/06/15
 */
@Service
@Slf4j
public class AppUserService implements WxUserService {

    @Resource
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public Long getIdByWxOpenid(String openid){
        Long[] success = {null};
        try {
            //开启新线程，防止阻塞
            Thread t = new Thread(()->{
                ServiceInstance instance = loadBalancerClient.choose("kingcent-auth");
                if(instance == null){
                    return;
                }
                String url = String.format("http://%s:%s/user/id_of_wx_openid/"+openid, instance.getHost(), instance.getPort());
                success[0] = restTemplate.getForObject(url,Long.class);
                log.info("url: {}", url);
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



    @Override
    public String getWxOpenid(Long userId){
        String[] success = {null};
        try {
            //开启新线程，防止阻塞
            Thread t = new Thread(()->{
                ServiceInstance instance = loadBalancerClient.choose("kingcent-auth");
                if(instance == null){
                    return;
                }
                String url = String.format("http://%s:%s/user/wx_openid/"+userId, instance.getHost(), instance.getPort());

                success[0] = restTemplate.getForObject(url,String.class);
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
