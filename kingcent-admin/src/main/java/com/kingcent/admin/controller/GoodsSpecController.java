package com.kingcent.admin.controller;

import com.kingcent.admin.entity.vo.EditSpecValVo;
import com.kingcent.admin.entity.vo.EditSpecVo;
import com.kingcent.admin.service.GoodsSpecService;
import com.kingcent.admin.service.GoodsSpecValueService;
import com.kingcent.common.result.Result;
import com.kingcent.common.shop.entity.vo.goods.GoodsSpecVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @PutMapping("/update_spec/{shopId}/{goodsId}/{specId}")
    public Result<?> updateSpec(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @PathVariable Long specId,
            @RequestBody EditSpecVo vo
    ){
        return goodsSpecService.update(shopId,goodsId, specId, vo);
    }

    @PutMapping("/update_spec_val/{shopId}/{goodsId}/{specId}/{specValId}")
    public Result<?> updateSpecVal(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @PathVariable Long specId,
            @PathVariable Long specValId,
            @RequestBody EditSpecValVo vo
    ){
        return goodsSpecValueService.update(shopId,goodsId, specId, specValId, vo);
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

    @DeleteMapping("/batch_delete/{shopId}/{goodsId}")
    @Transactional
    public Result<?> batchDelete(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @RequestParam(required = false) String specIds,
            @RequestParam(required = false) String specValIds
    ){
        List<Long> specIdsList = new ArrayList<>();
        if(specIds != null && specIds.length() > 0) for (String s : specIds.split(",")) {
            specIdsList.add(Long.valueOf(s));
        }
        List<Long> specValIdsList = new ArrayList<>();
        if(specValIds != null && specValIds.length() > 0) for (String s : specValIds.split(",")) {
            specValIdsList.add(Long.valueOf(s));
        }
        if( (specIdsList.size() == 0 || goodsSpecService.batchDelete(shopId, goodsId, specIdsList))
            && (specValIdsList.size() == 0 || goodsSpecValueService.batchDelete(shopId, goodsId, specValIdsList))
        )
            return Result.success("删除成功");
        return Result.fail("删除失败");
    }
}
