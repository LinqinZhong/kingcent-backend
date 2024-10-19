package com.kingcent;

import com.kingcent.common.wx.EnableWxService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableDiscoveryClient
@EnableWxService
@EnableScheduling
@MapperScan("com.kingcent.common.shop.mapper")
@Slf4j
public class ShopApplication{
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}