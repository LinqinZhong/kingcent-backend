package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.OrderService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
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
        //queryWrapper.in(OrderEntity::getStatus, -1, -4, -5);
        OrderEntity orderEntity = orderMapper.selectOne(queryWrapper);
        if (Objects.isNull(orderEntity)){
            return Result.fail("删除订单不存在");
        }
        if (orderEntity.getStatus().equals(1)){
            return Result.fail("待配送，无法删除该订单");
        }if (orderEntity.getStatus().equals(2)){
            return Result.fail("配送中，无法删除该订单");
        }if (orderEntity.getStatus().equals(3)){
            return Result.fail("已收货，无法删除该订单");
        }if (orderEntity.getStatus().equals(-2)){
            return Result.fail("已发送退款，无法删除该订单");
        }if (orderEntity.getStatus().equals(-3)){
            return Result.fail("商家同意退款，无法删除该订单");
        }
        remove(queryWrapper);
        return Result.success();
    }
}
