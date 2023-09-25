package com.kingcent.campus.admin.controller;

import com.kingcent.campus.admin.entity.vo.EditSkuVo;
import com.kingcent.campus.admin.service.GoodsSkuService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.GoodsSkuEntity;
import com.kingcent.campus.shop.entity.vo.goods.GoodsSkuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/27 10:06
 */
@RestController
@RequestMapping("/goods_sku")
public class GoodsSkuController {
    @Autowired
    private GoodsSkuService skuService;

    @GetMapping("/list/{shopId}/{goodsId}")
    public Result<List<GoodsSkuEntity>> list(@PathVariable Long shopId, @PathVariable Long goodsId){
        return skuService.list(shopId, goodsId);
    }

    @PostMapping("/create/{shopId}/{goodsId}")
    public Result<?> create(@PathVariable Long shopId, @PathVariable Long goodsId,  @RequestBody EditSkuVo vo){
        return skuService.create(shopId,goodsId,vo);
    }

    @PutMapping("/update/{shopId}/{goodsId}/{skuId}")
    public Result<?> update(@PathVariable Long shopId, @PathVariable Long goodsId, @PathVariable Long skuId, @RequestBody EditSkuVo vo){
        return skuService.update(shopId,goodsId,skuId, vo);
    }

    @DeleteMapping("/delete/{shopId}/{goodsId}/{skuId}")
    public Result<?> delete(@PathVariable Long shopId, @PathVariable Long goodsId, @PathVariable Long skuId){
        return skuService.delete(shopId, goodsId, skuId);
    }
}