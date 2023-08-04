package com.kingcent.campus;

import com.kingcent.campus.service.GroupService;
import com.kingcent.campus.service.SiteService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.kingcent.campus.mapper")
public class ShopApplication{
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ShopApplication.class, args);
        initPosition(ctx);
    }

    /**
     * 加载组点坐标
     */
    public static void initPosition(ConfigurableApplicationContext ctx){
        ctx.getBean(GroupService.class).initPointLocations();
        ctx.getBean(SiteService.class).initSiteLocations();
    }
}