package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.CategoryEntity;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.mapper.GoodsMapper;
import com.kingcent.campus.shop.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zzy
 */
@Service
@Slf4j
public class AdminGoodsServie extends ServiceImpl<GoodsMapper, GoodsEntity> implements GoodsService {


    @Override
    public void selectGoodsPage(Integer pageNum, Integer pageSize, GoodsEntity goodsEntity, CategoryEntity categoryEntity) {
        LambdaQueryWrapper<Object> queryWrapper = new LambdaQueryWrapper<>();

    }
}
