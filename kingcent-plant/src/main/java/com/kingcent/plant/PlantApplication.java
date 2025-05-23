package com.kingcent.plant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2025/2/4 22:47
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.kingcent.plant.mapper")
public class PlantApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlantApplication.class);
    }


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
