package com.kingcent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.shop.entity.OrderGoodsEntity;
import com.kingcent.common.shop.mapper.OrderGoodsMapper;
import com.kingcent.service.OrderGoodsService;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppOrderGoodsService extends ServiceImpl<OrderGoodsMapper, OrderGoodsEntity> implements OrderGoodsService {

    /**
     * 查询用户购买某些sku商品的总个数
     */
    @Override
    public Map<Long, Integer> countUserBuyCountOfSku(Long userId, Collection<Long> skuIds){
        Map<Long, Integer> res = new HashMap<>();
        List<OrderGoodsEntity> list = list(
                new QueryWrapper<OrderGoodsEntity>()
                        .eq("user_id", userId)
                        .in("sku_id", skuIds)
                        .select("sku_id, count")
        );
        for (OrderGoodsEntity goods : list) {
            Integer sku = res.get(goods.getSkuId());
            if (sku == null) res.put(goods.getSkuId(), goods.getCount());
            else{
                res.replace(goods.getSkuId(), sku + goods.getCount());
            }
        }
        return res;
    }
}