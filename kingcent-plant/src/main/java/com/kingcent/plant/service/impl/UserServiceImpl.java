package com.kingcent.plant.service.impl;

import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserEntity;
import com.kingcent.plant.service.UserService;
import jakarta.annotation.Resource;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author rainkyzhong
 * @date 2025/2/15 22:07
 */
@Service
public class UserServiceImpl implements UserService {


    @Resource
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Result<UserEntity> create(UserEntity userEntity) {
        ServiceInstance instance = loadBalancerClient.choose("kingcent-auth");
        if(instance == null){
            return Result.fail("服务器故障");
        }
        String url = String.format("http://%s:%s/user/create", instance.getHost(), instance.getPort());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserEntity> requestEntity = new HttpEntity<>(userEntity, headers);
        ResponseEntity<Result> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Result.class);
        Result body = response.getBody();
        if(!body.getSuccess()){
            return body;
        }
        LinkedHashMap<String, Object> data = (LinkedHashMap) body.getData();
        UserEntity user = new UserEntity();
        user.setUsername((String) data.get("username"));
        user.setId(Long.parseLong(data.get("id")+""));
        return Result.success(user);
    }
}
