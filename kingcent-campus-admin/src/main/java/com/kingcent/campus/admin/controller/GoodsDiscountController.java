package com.kingcent.campus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.admin.dto.EditGoodsDiscountDto;
import com.kingcent.campus.admin.service.GoodsDiscountService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.GoodsDiscountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author rainkyzhong
 * @date 2023/11/30 0:14
 */
@RequestMapping("/goods_discount")
@RestController
public class GoodsDiscountController {

    @Autowired
    private GoodsDiscountService discountService;

    @GetMapping("/list/{shopId}/{goodsId}/{page}/{pageSize}")
    public Result<VoList<GoodsDiscountEntity>> list(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @PathVariable Integer page,
            @PathVariable Integer pageSize
    ){
        return discountService.list(shopId, goodsId, page, pageSize);
    }

    @PostMapping("/save/{shopId}/{goodsId}")
    public Result<?> save(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @RequestBody EditGoodsDiscountDto dto
    ){
        return discountService.save(shopId, goodsId, dto);
    }

    @PutMapping("/save/{shopId}/{goodsId}/{discountId}")
    public Result<?> update(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @PathVariable Long discountId,
            @RequestBody EditGoodsDiscountDto dto
    ){
        return discountService.update(shopId, goodsId, discountId, dto);
    }

    @DeleteMapping("/delete/{shopId}/{goodsId}/{discountId}")
    public Result<?> delete(
            @PathVariable Long shopId,
            @PathVariable Long goodsId,
            @PathVariable Long discountId
    ){
        if(discountService.remove(
                new QueryWrapper<GoodsDiscountEntity>()
                        .eq("shop_id", shopId)
                        .eq("goods_id", goodsId)
                        .eq("id", discountId)
        )) return Result.success("删除成功");
        return Result.fail("删除失败");
    }
}
