package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.GoodsSkuEntity;
import com.kingcent.campus.shop.entity.GoodsSpecEntity;
import com.kingcent.campus.shop.entity.GoodsSpecValueEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsSkuInfoVo;
import com.kingcent.campus.shop.mapper.GoodsSkuMapper;
import com.kingcent.campus.service.GoodsSkuService;
import com.kingcent.campus.service.GoodsSpecService;
import com.kingcent.campus.service.GoodsSpecValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
@Slf4j
public class AppGoodsSkuService extends ServiceImpl<GoodsSkuMapper, GoodsSkuEntity> implements GoodsSkuService {

    @Autowired
    private GoodsSpecValueService goodsSpecValueService;
    @Autowired
    private GoodsSpecService goodsSpecService;

    @Override
    public GoodsSkuInfoVo fetchGoodsSkuInfo(Long goodsId){
        List<GoodsSpecEntity> goodsSpecs = goodsSpecService.list(new QueryWrapper<GoodsSpecEntity>().eq("goods_id", goodsId));
        List<GoodsSpecValueEntity> goodsSpecValues = goodsSpecValueService.list(new QueryWrapper<GoodsSpecValueEntity>().eq("goods_id",goodsId));
        List<GoodsSkuEntity> goodsSku = list(new QueryWrapper<GoodsSkuEntity>().eq("goods_id",goodsId).gt("safe_stock_quantity",0));
        GoodsSkuInfoVo vo = new GoodsSkuInfoVo();
        vo.init(goodsSpecs, goodsSpecValues, goodsSku);
        return vo;
    }

}