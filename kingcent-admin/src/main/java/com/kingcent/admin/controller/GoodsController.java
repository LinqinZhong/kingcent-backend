package com.kingcent.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.admin.service.GoodsService;
import com.kingcent.admin.service.GoodsSkuService;
import com.kingcent.admin.service.ShopService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.GoodsEntity;
import com.kingcent.common.shop.entity.GoodsSkuEntity;
import com.kingcent.common.shop.entity.vo.goods.GoodsInfoVo;
import com.kingcent.common.shop.entity.vo.goods.GoodsTableVo;
import com.kingcent.common.shop.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private GoodsSkuService skuService;

    @GetMapping("/list/{page}/{pageSize}")
    public Result<VoList<GoodsTableVo>> goodsList(
            @PathVariable Integer page,
            @PathVariable Integer pageSize,
            @RequestParam(required = false) String shopId,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer isSale,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
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
        if(isSale != null){
            wrapper.eq("is_sale", isSale);
        }

        Page<GoodsEntity> res = goodsService.page(pager, wrapper);
        List<GoodsTableVo> goodsVoList = new ArrayList<>();
        Set<Long> shopIds = new HashSet<>();
        for (GoodsEntity record : res.getRecords()) {
            shopIds.add(record.getShopId());
        }
        Map<Long, String> shopNames = shopService.getShopNames(shopIds);
        for (GoodsEntity record : res.getRecords()) {
            GoodsTableVo vo = BeanCopyUtils.copyBean(record, GoodsTableVo.class);
            vo.setShopName(shopNames.getOrDefault(record.getShopId(),""));
            goodsVoList.add(vo);
        }
        return Result.success(new VoList<>((int) res.getTotal(), goodsVoList));
    }

    @GetMapping("/info/{shopId}/{goodsId}")
    public Result<GoodsInfoVo> info(@PathVariable Long shopId, @PathVariable Long goodsId){
        return goodsService.info(shopId, goodsId);
    }

    @GetMapping("/is_sale/{shopId}/{goodsId}")
    public Result<Integer> isSale(@PathVariable Long shopId, @PathVariable Long goodsId){
        GoodsEntity goods = goodsService.getOne(
                new QueryWrapper<GoodsEntity>()
                        .eq("shop_id", shopId)
                        .eq("id", goodsId)
                        .select("is_sale")
        );
        if(goods == null || goods.getIsSale() != 1)
            return Result.success(0);
        return Result.success(1);
    }

    @PutMapping("/set_is_sale/{shopId}/{goodsId}/{isSale}")
    public Result<?> setIsSale(@PathVariable Long shopId, @PathVariable Long goodsId, @PathVariable Boolean isSale){
        if(isSale){
            Map<String, Object> sku = skuService.getMap(
                    new QueryWrapper<GoodsSkuEntity>()
                            .eq("goods_id", goodsId)
                            .select("COUNT(stock_quantity) AS stock")
            );
            if(sku == null || !sku.containsKey("stock") || (Long) sku.get("stock") == 0){
                return Result.fail("库存不足，无法上架");
            }
        }
        goodsService.update(new UpdateWrapper<GoodsEntity>()
                .eq("shop_id", shopId)
                .eq("id", goodsId)
                .set("is_sale", isSale)
        );
        return Result.success((isSale ? "上架" : "下架")+ "成功");
    }

    @DeleteMapping("/delete/{shopId}/{goodsId}")
    public Result<?> goodsList(@PathVariable Long shopId, @PathVariable Long goodsId){
        goodsService.remove(new QueryWrapper<GoodsEntity>()
                .eq("shop_id", shopId)
                .eq("id", goodsId)
        );
        return Result.success("删除成功");
    }

    @PostMapping("/create/{shopId}")
    public Result<?> create(@PathVariable Long shopId, @RequestBody GoodsInfoVo vo){
        return goodsService.save(shopId, vo);
    }
    @PutMapping("/update/{goodsId}")
    public Result<?> update(@PathVariable Long goodsId,@RequestBody GoodsInfoVo vo){
        return goodsService.update(goodsId, vo);
    }

}
