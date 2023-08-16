package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.OrderService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.constant.OrderStatus;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.vo.order.OrderStoreVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.listener.OrderListener;
import com.kingcent.campus.shop.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author zzy
 */
@Service
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
            case OrderStatus.REQUEST_TO_REFUND -> {
                return Result.fail("买家已发起退款，无法删除该订单");
            }
            case OrderStatus.AGREE_TO_REFUND -> {
                return Result.fail("等待买家退货，无法删除该订单");
            }
            case OrderStatus.BACKED -> {
                return Result.fail("等待卖家退款，无法删除该订单");
            }
        }
        remove(queryWrapper);
        return Result.success();
    }
}
