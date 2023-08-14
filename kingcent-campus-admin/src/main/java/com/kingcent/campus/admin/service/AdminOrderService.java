package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.vo.order.OrderStoreVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.listener.OrderListener;
import com.kingcent.campus.shop.mapper.OrderMapper;
import com.kingcent.campus.shop.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzy
 */
@Service
public class AdminOrderService extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {
    @Override
    public void listenOrderDead(OrderListener orderListener) {

    }

    @Override
    public boolean closeOrder(List<Long> orderIds) {
        return false;
    }

    @Override
    public VoList<OrderStoreVo> orderList(Long userId, Integer status, Integer page) {
        return null;
    }

    @Override
    public <T> Result<T> checkOrder(Long userId, Integer orderNum) {
        return null;
    }

    @Override
    public Result<?> createOrders(Long userId, Long loginId, PurchaseConfirmVo purchase) {
        return null;
    }

    @Override
    public List<OrderStoreVo> details(Long userId, List<Long> ids) {
        return null;
    }

    @Override
    public Result deleteOrder(Long id) {
        if (id == null){
            return Result.fail("该用户不存在");
        }
        LambdaQueryWrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderEntity::getId, id);
        remove(queryWrapper);
        return Result.success();
    }
}
