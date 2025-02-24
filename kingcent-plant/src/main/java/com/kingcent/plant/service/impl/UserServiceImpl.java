package com.kingcent.plant.service.impl;

import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserEntity;
import com.kingcent.plant.service.UserService;
import jakarta.annotation.Resource;
import org.apache.catalina.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
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

    private ServiceInstance getServiceInstance() throws KingcentSystemException {
        ServiceInstance instance = loadBalancerClient.choose("kingcent-auth");
        if(instance == null){
            throw new KingcentSystemException("服务器故障");
        }
        return instance;
    }

    @Override
    public Result<UserEntity> create(UserEntity userEntity) throws KingcentSystemException {
        ServiceInstance instance = getServiceInstance();
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

    @Override
    public Result<List<UserEntity>> getUserInfoByIds(Collection<Long> ids) throws KingcentSystemException {
        // 那边没有实现
        ServiceInstance instance = getServiceInstance();
        String url = String.format("http://%s:%s/user/info", instance.getHost(), instance.getPort());
        return restTemplate.getForObject(url, Result.class);
    }
}
