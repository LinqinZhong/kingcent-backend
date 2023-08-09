package com.kingcent.campus.shop.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.GoodsDiscountEntity;
import com.kingcent.campus.shop.mapper.GoodsDiscountMapper;
import com.kingcent.campus.shop.service.GoodsDiscountService;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
public class GoodsDiscountServiceImpl extends ServiceImpl<GoodsDiscountMapper, GoodsDiscountEntity> implements GoodsDiscountService {

    @Override
    public GoodsDiscountEntity getBestDiscount(Long goodsId, Integer count) {
        return null;
    }
}
