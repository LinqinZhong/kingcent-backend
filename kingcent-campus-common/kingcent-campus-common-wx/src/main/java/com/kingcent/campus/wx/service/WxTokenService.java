package com.kingcent.campus.wx.service;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.wx.config.WxConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2023/8/24 1:57
 */
@Service
@Slf4j
public class WxTokenService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired(required = false)
    private WxTokenStorageService wxTokenStorageService;

    private static final String API = "https://api.weixin.qq.com/cgi-bin/token";

    public String getWxAccessToken(String appId){
        String accessToken = null;
        if(wxTokenStorageService != null){
            accessToken = wxTokenStorageService.getWxToken(appId);
        }
        //token为空，重新加载
        if(accessToken == null){
            accessToken = fetchWxAccessToken(WxConfig.MINI_APP_ID, WxConfig.MINI_SECRET);
        }
        if(accessToken == null){
            log.error("获取accessToken失败");
            return null;
        }
        return accessToken;
    }

    public String fetchWxAccessToken(String appId, String secret){
        Map<String,Object> res = restTemplate.getForObject(API + "?appid=" + appId + "&secret=" + secret + "&grant_type=client_credential", Map.class);
        if(res != null && res.containsKey("access_token")){
            if(wxTokenStorageService != null){
                wxTokenStorageService.setWxToken(appId, (String) res.get("access_token"));
            }
            return (String) res.get("access_token");
        }
        return null;
    }
}
