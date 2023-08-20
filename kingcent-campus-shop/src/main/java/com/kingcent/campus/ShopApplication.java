package com.kingcent.campus;

import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.wx.EnableWxService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@EnableDiscoveryClient
@EnableWxService
@MapperScan("com.kingcent.campus.shop.mapper")
@Slf4j
public class ShopApplication{
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ShopApplication.class, args);

        OrderService orderService = ctx.getBean(OrderService.class);
        //订单超时监听
        new Thread(()->{
                orderService.listenOrderDead(orderIds -> {
                    List<Long> ids = new ArrayList<>();
                    for (String orderId : orderIds) {
                        ids.add(Long.valueOf(orderId));
                    }
                    log.info("订单超时自动关闭,{}->{}",orderIds,orderService.closeOrder(ids));
                });
        }).start();
    }
}