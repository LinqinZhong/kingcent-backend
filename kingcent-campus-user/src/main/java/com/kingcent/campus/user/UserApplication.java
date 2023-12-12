package com.kingcent.campus.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author rainkyzhong
 * @date 2023/12/12 12:07
 */
@SpringBootApplication
@MapperScan("com.kingcent.campus.user.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
