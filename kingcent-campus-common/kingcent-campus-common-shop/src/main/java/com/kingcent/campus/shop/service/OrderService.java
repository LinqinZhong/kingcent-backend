package com.kingcent.campus.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService extends IService<OrderEntity> {
    @Transactional
    Result<?> createOrders(Long userId, Long loginId, PurchaseConfirmVo purchase);
}
