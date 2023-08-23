package com.kingcent.campus.wx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2023/8/24 1:57
 */
@Service
public class WxTokenService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired(required = false)
    private WxTokenStorageService wxTokenStorageService;

    private static final String API = "https://api.weixin.qq.com/cgi-bin/token";

    public String fetchWxAccessToken(String appId, String secret){
        Map<String,Object> res = restTemplate.getForObject(API + "?appid=" + appId + "&secret=" + secret + "&grant_type=client_credential", Map.class);
        if(res != null && res.containsKey("access_token")){
            if(wxTokenStorageService != null){
                wxTokenStorageService.setWxToken(appId, secret);
            }
            return (String) res.get("access_token");
        }
        return null;
    }
}
