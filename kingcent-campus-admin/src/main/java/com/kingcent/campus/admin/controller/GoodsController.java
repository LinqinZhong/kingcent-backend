package com.kingcent.campus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.campus.admin.service.GoodsService;
import com.kingcent.campus.admin.service.ShopService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.entity.vo.goods.EditGoodsVo;
import com.kingcent.campus.shop.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShopService shopService;

    @GetMapping("/list/{page}/{pageSize}")
    public Result<VoList<GoodsEntity>> goodsList(
            @PathVariable Integer page,
            @PathVariable Integer pageSize,
            @RequestParam(required = false) String shopId,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime
    ) {
        Page<GoodsEntity> pager = new Page<>(page, pageSize,true);

        QueryWrapper<GoodsEntity> wrapper = new QueryWrapper<>();
        if(keywords != null)
            wrapper.like("name","%"+keywords+"%");
        if(categoryId != null)
            wrapper.eq("category_id", categoryId);
        if(shopId != null)
            wrapper.eq("shop_id", shopId);
        if(startTime != null)
            wrapper.ge("create_time", startTime);
        if(endTime != null)
            wrapper.le("create_time", endTime);

        Page<GoodsEntity> res = goodsService.page(pager, wrapper);
        return Result.success(new VoList<>((int) res.getTotal(), res.getRecords()));
    }

    @DeleteMapping("/delete/{goodsId}")
    public Result<?> goodsList(@PathVariable Long goodsId){
        goodsService.removeById(goodsId);
        return Result.success();
    }

    @PostMapping("/create/{shopId}")
    public Result<?> create(@PathVariable Long shopId, @PathVariable Long goodsId, EditGoodsVo vo){
        if(!shopService.exists(goodsId)) return Result.fail("店铺不存在");
        GoodsEntity goods = BeanCopyUtils.copyBean(vo, GoodsEntity.class);
        goods.setCreateTime(LocalDateTime.now());
        goods.setPrice(BigDecimal.valueOf(0));
        goods.setOriginalPrice(BigDecimal.valueOf(0));
        goods.setShopId(shopId);
        goods.setIsSale(0);
        goodsService.save(goods);
        return Result.success();
    }

    @PutMapping("/create/{goodsId}")
    public Result<?> update(@PathVariable Long goodsId, EditGoodsVo vo){
        GoodsEntity goods = BeanCopyUtils.copyBean(vo, GoodsEntity.class);
        goods.setId(goodsId);
        if (goodsService.updateById(goods)) {
            return Result.success();
        }
        return Result.fail("商品不存在");
    }

}
