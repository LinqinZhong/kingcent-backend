package com.kingcent.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
/**
 * 鉴权配置
 * @author rainkyzhong
 * @date 2023/06/15
 */
@Configuration
@ConfigurationProperties(prefix = "auth")
@EnableConfigurationProperties(AuthenticationConfig.class)
public class AuthenticationConfig {
    private List<String> ignorePath = new ArrayList<>();

    public List<String> getIgnorePath() {
        return ignorePath;
    }

    public void setIgnorePath(List<String> ignorePath) {
        this.ignorePath = ignorePath;
    }
}
