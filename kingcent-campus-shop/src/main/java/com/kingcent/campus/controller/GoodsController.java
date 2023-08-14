package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.constant.GoodsSortType;
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
     * @param page 页
     * @param pageSize 页大小
     */
    @GetMapping("/fetch/{page}/{pageSize}")
    @ResponseBody
    public Result<List<GoodsVo>> fetch(
            @RequestParam(required = false) Long groupId,
            @PathVariable Integer page,
            @PathVariable Integer pageSize
    ){
        List<GoodsVo> list = goodsService.getGoodsList(groupId, page, pageSize);
        if (list != null) return Result.success(list);
        return Result.fail("获取失败");
    }

    /**
     * 搜索商品
     * @param groupId 收货点id
     * @param categoryId 分类id
     * @param keywords 关键词
     * @param sortType 排序方式
     * @param deliveryToday 是否今天送达
     * @param freeForDelivery 是否免配送费
     * @param page 页
     * @param pageSize 分页大小
     */
    @GetMapping("/search/{groupId}/{page}/{pageSize}")
    @ResponseBody
    public Result<List<GoodsVo>> search(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) GoodsSortType sortType,
            @RequestParam(required = false) Boolean deliveryToday,
            @RequestParam(required = false) Boolean freeForDelivery,
            @PathVariable Long groupId,
            @PathVariable Integer page,
            @PathVariable Integer pageSize
    ){
        List<GoodsVo> list = goodsService.searchGoods(groupId, keywords, page, pageSize, categoryId, sortType, deliveryToday, freeForDelivery);
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