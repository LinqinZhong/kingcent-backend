package com.kingcent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.shop.entity.GoodsDiscountEntity;

public interface GoodsDiscountService extends IService<GoodsDiscountEntity> {
    GoodsDiscountEntity getBestDiscount(Long goodsId, Integer count);
}
