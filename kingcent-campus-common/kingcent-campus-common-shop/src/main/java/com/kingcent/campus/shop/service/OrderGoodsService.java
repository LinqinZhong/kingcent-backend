package com.kingcent.campus.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.OrderGoodsEntity;

import java.util.Collection;
import java.util.Map;

public interface OrderGoodsService extends IService<OrderGoodsEntity> {
    Map<Long, Integer> countUserBuyCountOfSku(Long userId, Collection<Long> skuIds);
}
