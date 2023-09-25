package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.GoodsService;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.mapper.GoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzy
 */
@Service
@Slf4j
public class AdminGoodsService extends ServiceImpl<GoodsMapper, GoodsEntity> implements GoodsService {

    @Override
    public boolean exist(Long shopId, Long goodsId) {
        return baseMapper.exists(
                new QueryWrapper<GoodsEntity>()
                        .eq("shop_id", shopId)
                        .eq("id",goodsId)
        );
    }
}
