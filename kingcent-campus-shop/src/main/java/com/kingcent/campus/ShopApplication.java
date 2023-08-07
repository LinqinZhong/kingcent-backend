package com.kingcent.campus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.kingcent.campus.shop.mapper")
public class ShopApplication{
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}