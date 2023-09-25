package com.kingcent.campus.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.campus.admin.service.GoodsService;
import com.kingcent.campus.admin.service.GoodsSkuService;
import com.kingcent.campus.admin.service.ShopService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.GoodsEntity;
import com.kingcent.campus.shop.entity.GoodsSkuEntity;
import com.kingcent.campus.shop.entity.ShopEntity;
import com.kingcent.campus.shop.entity.vo.goods.EditGoodsVo;
import com.kingcent.campus.shop.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

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
    public Result<?> create(@PathVariable Long shopId, @RequestBody EditGoodsVo vo){
        if(!shopService.exists(shopId)) return Result.fail("店铺不存在");
        if(vo.getName() == null) return Result.fail("商品名称不能为空");
        if(vo.getThumbnail() == null) return Result.fail("商品缩略图不能为空");
        GoodsEntity goods = BeanCopyUtils.copyBean(vo, GoodsEntity.class);
        goods.setCreateTime(LocalDateTime.now());
        goods.setPrice(BigDecimal.valueOf(0));
        goods.setName(vo.getName());
        goods.setImages(String.join(",",vo.getImages()));
        goods.setOriginalPrice(BigDecimal.valueOf(0));
        goods.setDescription(String.join(",",vo.getDescription()));
        goods.setShopId(shopId);
        goods.setIsSale(0);
        goodsService.save(goods);
        return Result.success("创建成功");
    }
    @PutMapping("/update/{goodsId}")
    public Result<?> update(@PathVariable Long goodsId,@RequestBody EditGoodsVo vo){
        GoodsEntity goods = BeanCopyUtils.copyBean(vo, GoodsEntity.class);
        goods.setId(goodsId);
        goods.setImages(String.join(",",vo.getImages()));
        goods.setDescription(String.join(",",vo.getDescription()));
        if (goodsService.updateById(goods)) {
            return Result.success("修改成功");
        }
        return Result.fail("商品不存在");
    }

}
