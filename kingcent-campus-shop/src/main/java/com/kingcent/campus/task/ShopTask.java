package com.kingcent.campus.task;

import com.kingcent.campus.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author rainkyzhong
 * @date 2023/8/25 6:32
 */
@Component
public class ShopTask {

    @Autowired
    private OrderService orderService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void autoCloseOrder(){
        orderService.closeDeadOrder();
    }
}
