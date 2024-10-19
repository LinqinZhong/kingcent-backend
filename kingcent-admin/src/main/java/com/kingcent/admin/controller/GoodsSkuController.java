package com.kingcent.admin.controller;

import com.kingcent.admin.entity.vo.EditSkuVo;
import com.kingcent.admin.service.GoodsSkuService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.GoodsSkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2023/8/27 10:06
 */
@RestController
@RequestMapping("/goods_sku")
public class GoodsSkuController {
    @Autowired
    private GoodsSkuService skuService;

    @GetMapping("/list/{shopId}/{goodsId}/{page}/{pageSize}")
    public Result<VoList<GoodsSkuEntity>> list(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @PathVariable Integer page,
            @PathVariable Integer pageSize
    ){
        return skuService.list(shopId, goodsId, page, pageSize);
    }

    @PostMapping("/save/{shopId}/{goodsId}")
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