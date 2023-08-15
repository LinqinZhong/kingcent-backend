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
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzy
 */
@Service
public class AdminOrderService extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Override
    public Result<?> deleteOrder(Long id) {
        if (id == null){
            return Result.fail("该用户不存在");
        }
        LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEntity::getId, id);
        remove(queryWrapper);
        return Result.success();
    }
}
