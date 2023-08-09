package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.*;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.mapper.OrderMapper;
import com.kingcent.campus.shop.service.*;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Override
    public Result<?> createOrders(Long userId, Long loginId, PurchaseConfirmVo purchase) {
        return null;
    }
}
