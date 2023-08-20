package com.kingcent.campus.admin;

import com.kingcent.campus.wx.EnableWxService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableWxService
@SpringBootApplication
@MapperScan("com.kingcent.campus.shop.mapper")
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
