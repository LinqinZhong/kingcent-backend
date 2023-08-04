package com.kingcent.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kingcent.campus.common.entity.*;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.goods.GoodsDetailsVo;
import com.kingcent.campus.entity.vo.goods.GoodsPageVo;
import com.kingcent.campus.entity.vo.goods.GoodsVo;
import com.kingcent.campus.service.*;
import com.kingcent.campus.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsSpecService goodsSpecService;

    @Autowired
    private GoodsSkuService goodsSkuService;

    @Autowired
    private GoodsSpecValueService goodsSpecValueService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private DeliveryGroupService deliveryGroupService;


    /**
     * 获取商城商品列表
     * @param groupId 配送点
     * @param key  关键词
     * @param page 页
     * @param pageSize 页大小
     */
    @GetMapping("/fetch/{groupId}/{key}/{page}/{pageSize}")
    @ResponseBody
    public Result<List<GoodsVo>> fetch(
            @PathVariable Long groupId,
            @PathVariable String key,
            @PathVariable Integer page,
            @PathVariable Integer pageSize
    ){
        if(pageSize > 10 || pageSize < 1) pageSize = 1;
        //获取能配送到该配送点的商铺
        List<DeliveryGroup> deliveryGroups = deliveryGroupService.list(
                new QueryWrapper<DeliveryGroup>()
                        .eq("group_id", groupId)
        );
        if(deliveryGroups.size() == 0){
            return Result.success(new ArrayList<>());
        }

        //TODO redis做缓存

        //提取商铺编号和运费
        Map<Long, BigDecimal> deliveryGroupFreight = new HashMap<>();
        List<Long> shopIds = new ArrayList<>();
        for (DeliveryGroup deliveryGroup : deliveryGroups) {
            shopIds.add(deliveryGroup.getShopId());
            deliveryGroupFreight.put(deliveryGroup.getShopId(), deliveryGroup.getDeliveryFee());
        }
        List<GoodsVo> res = new ArrayList<>();
        Page<GoodsEntity> pager = new Page<>( (long) pageSize *(page-1),pageSize);
        goodsService.page(pager, new QueryWrapper<GoodsEntity>().in("shop_id", shopIds));
        for (GoodsEntity record : pager.getRecords()) {
            List<String> tags = new ArrayList<>();
            GoodsVo goodsVo = new GoodsVo();
            goodsVo.setId(record.getId());
            goodsVo.setTags(tags);
            goodsVo.setPrice(record.getPrice());
            goodsVo.setOriginalPrice(record.getOriginalPrice());
            goodsVo.setName(record.getName());
            goodsVo.setThumbnail(record.getThumbnail());

            //【免费配送】标签
            if (deliveryGroupFreight.containsKey(record.getShopId()) && deliveryGroupFreight.get(record.getShopId()).doubleValue() == 0D){
                tags.add("免费配送");
            }


            res.add(goodsVo);
        }
        return Result.success(res);
    }

    /**
     * 获取商品信息
     * @param spuId 商品id
     */
    @GetMapping("/details/{spu_id}")
    @ResponseBody
    public Result<GoodsDetailsVo> details(
            @PathVariable("spu_id") Long spuId
    ){
        GoodsEntity goods = goodsService.getById(spuId);
        List<GoodsSpecEntity> goodsSpecs = goodsSpecService.list(new QueryWrapper<GoodsSpecEntity>().eq("goods_id", spuId));
        List<GoodsSpecValueEntity> goodsSpecValues = goodsSpecValueService.list(new QueryWrapper<GoodsSpecValueEntity>().eq("goods_id",spuId));
        List<GoodsSkuEntity> goodsSku = goodsSkuService.list(new QueryWrapper<GoodsSkuEntity>().eq("goods_id",spuId).gt("safe_stock_quantity",0));
        GoodsDetailsVo vo = new GoodsDetailsVo();
        vo.init(goods, goodsSpecs, goodsSpecValues, goodsSku);
        return Result.success(vo);
    }

    /**
     * 获取购买商品信息
     * @param goodsId 商品id
     * @param count 购买数量
     * @param sku 规格
     */
    @GetMapping("/get_purchase_info")
    @ResponseBody
    public Result<?> getPurchaseInfo(
            HttpServletRequest request,
            @RequestParam Long goodsId,
            @RequestParam Integer count,
            @RequestParam String sku
    ){
        return goodsService.getPurchaseInfo(RequestUtil.getUserId(request), goodsId, URLDecoder.decode(sku, StandardCharsets.UTF_8), count);
    }

    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓管理端代码↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    /**
     * 获取管理端商品列表
     * @param page 页
     * @param pageSize 页大小
     */
    @GetMapping("/list/{page}/{page_size}")
    @ResponseBody
    public Result<GoodsPageVo> list(
            @PathVariable("page") Integer page,
            @PathVariable("page_size") Integer pageSize,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ){
        if(pageSize > 10 || pageSize < 1) pageSize = 1;
        Page<GoodsEntity> pager = new Page<>( (long) pageSize *(page-1),pageSize);
        QueryWrapper<GoodsEntity> wrapper = new QueryWrapper<>();
        //关键词
        if(keywords != null) wrapper.like("name", "%"+keywords+"%");
        //开始时间
        if(startTime != null) wrapper.ge("create_time", startTime);
        //结束时间
        if(endTime != null) wrapper.le("create_time", endTime);
        GoodsPageVo res = new GoodsPageVo();
        res.setTotal(goodsService.count(wrapper));
        goodsService.page(pager,wrapper);
        res.setList(pager.getRecords());
        res.setCurrent(page);
        return Result.success(res);
    }

    /**
     * 管理端删除商品
     */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result<?> delete(@PathVariable Long id){
        if (goodsService.removeById(id)){
            return Result.success();
        }
        return Result.fail("删除失败");
    }
}