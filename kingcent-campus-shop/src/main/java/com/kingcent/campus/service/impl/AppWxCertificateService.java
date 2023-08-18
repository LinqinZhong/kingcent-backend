package com.kingcent.campus.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * @author rainkyzhong
 * @date 2023/8/19 1:14
 * @warning 支付敏感信息，禁止外传，侵权必究
 */
public class AppWxCertificateService {

    private static final String API = "https://api.mch.weixin.qq.com/v3/certificates";

    @Autowired
    private RestTemplate restTemplate;

    public String get(){
        restTemplate.
    }
}
