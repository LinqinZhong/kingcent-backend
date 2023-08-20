package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.OrderService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.constant.OrderStatus;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zzy
 */
@Service
@Slf4j
public class AdminOrderService extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {


    @Autowired
    private OrderMapper orderMapper;



    @Override
    public Result<?> deleteOrder(Long id) {

        if (id == null){
            return Result.fail("id不能为空");
        }
        LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEntity::getId, id);
        OrderEntity orderEntity = orderMapper.selectOne(queryWrapper);
        if (orderEntity == null){
            return Result.fail("删除订单不存在");
        }
        switch (orderEntity.getStatus()){
            case OrderStatus.READY -> {
                return Result.fail("订单交易未结束，无法删除");
            }
            case OrderStatus.DELIVERING -> {
                return Result.fail("订单配送，无法删除");
            }
            case OrderStatus.RECEIVED -> {
                return Result.fail("订单售后保障中，无法删除");
            }
            case OrderStatus.REFUNDING -> {
                return Result.fail("买家申请退款中，无法删除");
            }
        }
        remove(queryWrapper);
        return Result.success();
    }
}
