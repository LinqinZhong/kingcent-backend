package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.goods.GoodsSkuInfoVo;
import com.kingcent.campus.shop.service.GoodsSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sku")
public class GoodsSkuController {

    @Autowired
    private GoodsSkuService goodsSkuService;

    /**
     * 获取商品的规格
     * @param goodsId 商品id
     */
    @GetMapping("/fetch/{goodsId}")
    public Result<GoodsSkuInfoVo> fetchGoodsSkuInfo(@PathVariable("goodsId") Long goodsId){
        GoodsSkuInfoVo goodsSkuInfoVo = goodsSkuService.fetchGoodsSkuInfo(goodsId);
        if (goodsSkuInfoVo != null) return Result.success(goodsSkuInfoVo);
        return Result.fail("获取失败");
    }
}
