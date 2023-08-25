package com.kingcent.campus.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.kingcent.campus.auth.mapper")
public class AuthApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(AuthApplication.class, args);

        System.out.println("测试连接Redis："+run.getBean(StringRedisTemplate.class).keys("*"));
    }

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(
                Arrays.asList(
                    MediaType.TEXT_HTML,
                    MediaType.TEXT_PLAIN
                )
        );
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);

        return restTemplate;
    }
}
