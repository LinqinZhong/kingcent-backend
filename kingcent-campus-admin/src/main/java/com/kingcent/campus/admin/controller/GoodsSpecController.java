package com.kingcent.campus.admin.controller;

import com.kingcent.campus.admin.entity.vo.EditSpecValVo;
import com.kingcent.campus.admin.entity.vo.EditSpecVo;
import com.kingcent.campus.admin.service.GoodsSpecService;
import com.kingcent.campus.admin.service.GoodsSpecValueService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.goods.GoodsSpecVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/27 7:44
 */
@RestController
@RequestMapping("/goods_spec")
public class GoodsSpecController {

    @Autowired
    private GoodsSpecService goodsSpecService;
    @Autowired
    private GoodsSpecValueService goodsSpecValueService;

    @PutMapping("/update_spec/{shopId}/{specId}")
    public Result<?> updateSpec(
            @PathVariable Long shopId,
            @PathVariable Long specId,
            @RequestBody EditSpecVo vo
    ){
        return goodsSpecService.update(shopId, specId, vo);
    }

    @PutMapping("/update_spec_val/{shopId}/{specValId}")
    public Result<?> updateSpecVal(
            @PathVariable Long shopId,
            @PathVariable Long specValId,
            @RequestBody EditSpecValVo vo
    ){
        return goodsSpecValueService.update(shopId, specValId, vo);
    }

    @PostMapping("/create_spec/{shopId}/{goodsId}")
    public Result<?> createSpec(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @RequestBody EditSpecVo vo
    ){
        return goodsSpecService.create(shopId, goodsId, vo);
    }

    @PostMapping("/create_spec_val/{shopId}/{goodsId}/{specId}")
    public Result<?> createSpecVal(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @PathVariable Long specId,
            @RequestBody EditSpecValVo vo
    ){
        return goodsSpecValueService.create(shopId, goodsId, specId,  vo);
    }

    @GetMapping("/list/{shopId}/{goodsId}")
    public Result<List<GoodsSpecVo>> specList(@PathVariable Long shopId, @PathVariable Long goodsId){
        return goodsSpecService.getSpecList(shopId, goodsId);
    }

    @DeleteMapping("/delete_spec/{shopId}/{goodsId}/{specId}")
    public Result<?> delSpec(@PathVariable Long shopId, @PathVariable Long goodsId, @PathVariable Long specId){
        return goodsSpecService.delete(shopId, goodsId, specId);
    }

    @DeleteMapping("/delete_spec_val/{shopId}/{goodsId}/{specId}/{specValId}")
    public Result<?> delSpecVal(@PathVariable Long shopId, @PathVariable Long goodsId, @PathVariable Long specId, @PathVariable Long specValId){
        return goodsSpecValueService.delete(shopId, goodsId, specId, specValId);
    }
}
