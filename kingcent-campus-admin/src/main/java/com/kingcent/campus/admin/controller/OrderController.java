package com.kingcent.campus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kingcent.campus.admin.service.OrderService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zzy
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Result<List<OrderEntity>> list(){
        LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        //后续需要啥条件在加

        List<OrderEntity> list = orderService.list(queryWrapper);
        return Result.success(list);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteOrder(@PathVariable Long id){
        return orderService.deleteOrder(id);
    }

}
