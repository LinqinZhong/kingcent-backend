package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.goods.GoodsDetailsVo;
import com.kingcent.campus.shop.entity.vo.goods.GoodsVo;
import com.kingcent.campus.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 获取商城商品列表
     * @param groupId 配送点
     * @param key  关键词
     * @param page 页
     * @param pageSize 页大小
     */
    @GetMapping("/fetch/{key}/{page}/{pageSize}")
    @ResponseBody
    public Result<List<GoodsVo>> fetch(
            @RequestParam(required = false) Long groupId,
            @PathVariable String key,
            @PathVariable Integer page,
            @PathVariable Integer pageSize
    ){
        List<GoodsVo> list = goodsService.getGoodsList(groupId, key, page, pageSize);
        if (list != null) return Result.success(list);
        return Result.fail("获取失败");
    }

    /**
     * 获取商品信息
     * @param goodsId 商品id
     */
    @GetMapping("/details/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailsVo> details(@PathVariable Long goodsId){
        GoodsDetailsVo details = goodsService.details(goodsId);
        if (details != null) return Result.success(details);
        return Result.fail("获取失败");
    }
}