package com.kingcent.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.shop.entity.GoodsDiscountEntity;
import com.kingcent.campus.shop.service.impl.GoodsDiscountServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class AppShopGoodsDiscountService extends GoodsDiscountServiceImpl {

    @Override
    public GoodsDiscountEntity getBestDiscount(Long goodsId, Integer count){

        //查出more_than范围
        Map<String,Object> countRange = getMap(new QueryWrapper<GoodsDiscountEntity>()
                .eq("goods_id", goodsId)
                .select("MAX(more_than) AS max, MIN(more_than) AS min")
                .le("more_than", count)
                .ge("deadline", LocalDateTime.now())
        );
        if(countRange == null || (Integer) countRange.get("min") > count) return null;

        //查出最大more_than的选项
        return getOne(
                new QueryWrapper<GoodsDiscountEntity>()
                        .eq("goods_id", goodsId)
                        .eq("more_than", countRange.get("max"))
                        .ge("deadline", LocalDateTime.now())
                        .last("limit 1")
        );
    }

}
