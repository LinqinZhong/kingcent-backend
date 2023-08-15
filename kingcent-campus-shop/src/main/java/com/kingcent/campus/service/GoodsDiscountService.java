package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.GoodsDiscountEntity;

public interface GoodsDiscountService extends IService<GoodsDiscountEntity> {
    GoodsDiscountEntity getBestDiscount(Long goodsId, Integer count);
}
