package com.kingcent.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author rainkyzhong
 * @date 2025/2/15 3:43
 */
@Component
public class LoginConfig {

    // 加密token对象用的key
    @Value("abcdefghijklmn")
    public String TOKEN_KEY;
    // 加密authorization用的key
    @Value("asfgrdhfdgsfadawge")
    public String AUTHORIZATION_KEY;
}
