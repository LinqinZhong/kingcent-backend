package com.kingcent.net;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.kingcent.net.mapper")
@SpringBootApplication
public class NetApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetApplication.class, args);
    }

}
