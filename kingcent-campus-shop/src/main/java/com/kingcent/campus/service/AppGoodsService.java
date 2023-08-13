package com.kingcent.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.*;
import com.kingcent.campus.shop.entity.vo.goods.GoodsDetailsVo;
import com.kingcent.campus.shop.entity.vo.goods.GoodsVo;
import com.kingcent.campus.shop.mapper.GoodsMapper;
import com.kingcent.campus.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class AppGoodsService extends ServiceImpl<GoodsMapper, GoodsEntity> implements GoodsService {


    @Autowired
    private GoodsSpecService goodsSpecService;

    @Autowired
    private GoodsSkuService goodsSkuService;

    @Autowired
    private GoodsSpecValueService goodsSpecValueService;

    @Autowired
    private DeliveryGroupService deliveryGroupService;

    @Autowired
    private GoodsDiscountService goodsDiscountService;


    @Override
    public List<GoodsVo> getGoodsList(Long groupId, String key, Integer page, Integer pageSize){

        if(pageSize > 10 || pageSize < 1) pageSize = 1;

        //TODO redis做缓存
        //获取商铺
        QueryWrapper<DeliveryGroup> wrapper = new QueryWrapper<>();
        wrapper.select("any_value(shop_id) as shop_id, any_value(group_id) as_group_id, any_value(delivery_fee) as delivery_id");
        //指定配送点
        if (groupId != null) wrapper.eq("group_id", groupId);
            //不指定配送点，按商铺id分组，以免出现多条相同商铺的
        else wrapper.groupBy("shop_id");
        List<DeliveryGroup> deliveryGroups = deliveryGroupService.list(wrapper);
        if(deliveryGroups.size() == 0){
            return new ArrayList<>();
        }

        //提取商铺编号和运费
        Map<Long, BigDecimal> deliveryGroupFreight = new HashMap<>();
        List<Long> shopIds = new ArrayList<>();
        for (DeliveryGroup deliveryGroup : deliveryGroups) {
            shopIds.add(deliveryGroup.getShopId());

            //设置了配送点才有运费信息
            if(groupId != null && deliveryGroup.getDeliveryFee() != null)
                deliveryGroupFreight.put(deliveryGroup.getShopId(), deliveryGroup.getDeliveryFee());
        }

        //获取商品列表
        Page<GoodsEntity> pager = new Page<>( (long) pageSize *(page-1),pageSize);
        page(pager, new QueryWrapper<GoodsEntity>().in("shop_id", shopIds));

        //整合数据，提取商品id
        Map<Long, GoodsVo> goodsVoMap = new HashMap<>();
        List<Long> goodsIds = new ArrayList<>();
        List<GoodsVo> res = new ArrayList<>();
        for (GoodsEntity record : pager.getRecords()) {
            List<String> tags = new ArrayList<>();
            GoodsVo goodsVo = new GoodsVo();
            goodsVo.setId(record.getId());
            goodsVo.setTags(tags);
            goodsVo.setPrice(record.getPrice());
            goodsVo.setOriginalPrice(record.getOriginalPrice());
            goodsVo.setName(record.getName());
            goodsVo.setThumbnail(record.getThumbnail());
            goodsIds.add(record.getId());
            goodsVoMap.put(record.getId(), goodsVo);

            //【免费配送】标签
            if (deliveryGroupFreight.containsKey(record.getShopId())  && deliveryGroupFreight.get(record.getShopId()).doubleValue() == 0D){
                tags.add("免费配送");
            }
            res.add(goodsVo);
        }

        //TODO redis做缓存
        //获取折扣信息
        List<GoodsDiscountEntity> discounts = goodsDiscountService.list(new QueryWrapper<GoodsDiscountEntity>()
                .in("goods_id", goodsIds)
                .gt("deadline", LocalDateTime.now().plusHours(2))
                .select("any_value(more_than) as more_than, any_value(goods_id) as goods_id, any_value(num) as num, any_value(type) as type")
                .groupBy("goods_id")
                .orderByDesc("more_than")
        );
        for (GoodsDiscountEntity discount : discounts) {
            if (discount.getType() == 1){
                goodsVoMap.get(discount.getGoodsId()).getTags().add(discount.getMoreThan()+"件减"+discount.getNum());
            }else if (discount.getType() == 2){
                goodsVoMap.get(discount.getGoodsId()).getTags().add(discount.getMoreThan()+"件"+discount.getNum().doubleValue()*10+"折");
            }
        }

        return res;
    }

    @Override
    public GoodsDetailsVo details(Long goodsId){
        GoodsEntity goods = getById(goodsId);
        List<GoodsSpecEntity> goodsSpecs = goodsSpecService.list(new QueryWrapper<GoodsSpecEntity>().eq("goods_id", goodsId));
        List<GoodsSpecValueEntity> goodsSpecValues = goodsSpecValueService.list(new QueryWrapper<GoodsSpecValueEntity>().eq("goods_id",goodsId));
        List<GoodsSkuEntity> goodsSku = goodsSkuService.list(new QueryWrapper<GoodsSkuEntity>().eq("goods_id",goodsId).gt("safe_stock_quantity",0));
        GoodsDetailsVo vo = new GoodsDetailsVo();
        vo.init(goods, goodsSpecs, goodsSpecValues, goodsSku);
        return vo;
    }

    @Override
    public void selectGoodsPage(Integer pageNum, Integer pageSize, GoodsEntity goodsEntity, CategoryEntity categoryEntity) {

    }

}
