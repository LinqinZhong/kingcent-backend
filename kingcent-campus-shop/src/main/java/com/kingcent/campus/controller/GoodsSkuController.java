package com.kingcent.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.common.entity.GoodsSkuEntity;
import com.kingcent.campus.common.entity.GoodsSpecEntity;
import com.kingcent.campus.common.entity.GoodsSpecValueEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.goods.GoodsSkuInfoVo;
import com.kingcent.campus.service.GoodsSkuService;
import com.kingcent.campus.service.GoodsSpecService;
import com.kingcent.campus.service.GoodsSpecValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sku")
public class GoodsSkuController {
    @Autowired
    private GoodsSpecValueService goodsSpecValueService;
    @Autowired
    private GoodsSpecService goodsSpecService;
    @Autowired
    private GoodsSkuService goodsSkuService;

    /**
     * 获取商品的规格
     * @param goodsId 商品id
     */
    @GetMapping("/fetch/{goodsId}")
    public Result<GoodsSkuInfoVo> fetchGoodsSkuInfo(@PathVariable("goodsId") Long goodsId){
        List<GoodsSpecEntity> goodsSpecs = goodsSpecService.list(new QueryWrapper<GoodsSpecEntity>().eq("goods_id", goodsId));
        List<GoodsSpecValueEntity> goodsSpecValues = goodsSpecValueService.list(new QueryWrapper<GoodsSpecValueEntity>().eq("goods_id",goodsId));
        List<GoodsSkuEntity> goodsSku = goodsSkuService.list(new QueryWrapper<GoodsSkuEntity>().eq("goods_id",goodsId).gt("safe_stock_quantity",0));
        GoodsSkuInfoVo vo = new GoodsSkuInfoVo();
        vo.init(goodsSpecs, goodsSpecValues, goodsSku);
        return Result.success(vo);
    }
}
