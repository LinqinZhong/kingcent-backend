package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.*;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.AddressVo;
import com.kingcent.campus.entity.vo.purchase.PurchaseGoodsVo;
import com.kingcent.campus.entity.vo.purchase.PurchaseInfoVo;
import com.kingcent.campus.entity.vo.purchase.PurchaseStoreVo;
import com.kingcent.campus.mapper.GoodsMapper;
import com.kingcent.campus.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, GoodsEntity> implements GoodsService {
    @Autowired
    private GoodsSkuService skuService;
    @Autowired
    private ShopService shopService;

    @Autowired
    private DeliveryTemplateService deliveryTemplateService;

    @Autowired
    private GoodsDiscountService goodsDiscountService;

    @Autowired
    private AddressService addressService;

    @Override
    public Result<PurchaseInfoVo> getPurchaseInfo(Long userId, Long goodsId, String specInfo, Integer count){

        //1.获取商品
        GoodsEntity goods = getById(goodsId);
        if(goods == null) return Result.fail("商品不存在", PurchaseInfoVo.class);

        //2.获取商品规格
        GoodsSkuEntity sku = skuService.getOne(
                new QueryWrapper<GoodsSkuEntity>()
                        .eq("goods_id", goodsId)
                        .eq("spec_info", specInfo)
                        .last("limit 1")
        );
        if(sku == null) return Result.fail("该规格的商品不存在", PurchaseInfoVo.class);

        //3.获取店铺信息
        ShopEntity shopEntity = shopService.getById(goods.getShopId());
        if(shopEntity == null) return Result.fail("该商店不存在", PurchaseInfoVo.class);

        //4.获取商品折扣信息
        GoodsDiscountEntity discount = goodsDiscountService.getOne(
                new QueryWrapper<GoodsDiscountEntity>()
                        .eq("goods_id", goodsId)
                        .le("more_than", count)
                        .orderBy(true, false,"more_than")
                        .last("limit 1")
        );

        //5.获取配送信息
        DeliveryTemplateEntity delivery = deliveryTemplateService.getOne(new QueryWrapper<DeliveryTemplateEntity>()
                .eq("shop_id", goods.getShopId())
                .eq("is_used", 1)
                .last("limit 1")
        );

        //6.获取地址信息
        List<AddressVo> userAddress = addressService.getUserAddress(userId);



        //获取不到配送信息
        if(delivery == null){
            return Result.fail("店铺已休息", PurchaseInfoVo.class);
        }

        //创建购买信息
        PurchaseInfoVo purchaseInfoVo = new PurchaseInfoVo();
        PurchaseStoreVo purchaseStoreVo = new PurchaseStoreVo();
        PurchaseGoodsVo purchaseGoodsVo = new PurchaseGoodsVo();
        List<PurchaseStoreVo> storeInfoList = new ArrayList<>();
        List<PurchaseGoodsVo> goodsInfoList = new ArrayList<>();
        goodsInfoList.add(purchaseGoodsVo);
        purchaseStoreVo.setName(shopEntity.getName());
        purchaseGoodsVo.setCount(count);
        purchaseGoodsVo.setId(goodsId);
        purchaseGoodsVo.setTitle(goods.getName());
        purchaseStoreVo.setId(shopEntity.getId());
        purchaseStoreVo.setAllowedAddress(delivery.getAllowedAddress());
        storeInfoList.add(purchaseStoreVo);
        purchaseStoreVo.setGoodsList(goodsInfoList);
        BigDecimal price = sku.getPrice().multiply(BigDecimal.valueOf(count));
        purchaseGoodsVo.setPrice(sku.getPrice());
        purchaseStoreVo.setDeliveryFee(BigDecimal.valueOf(1));
        purchaseInfoVo.setTotalPrice(price);
        purchaseGoodsVo.setSkuDesc(sku.getDescription());
        purchaseGoodsVo.setSku(specInfo);
        purchaseGoodsVo.setThumbnail(sku.getImage());
        purchaseInfoVo.setAddressList(userAddress);
        if(discount != null){
            if (discount.getType() == 1){
                //满减
                price = price.subtract(discount.getNum());
                purchaseInfoVo.setDiscountPrice(discount.getNum());
                purchaseStoreVo.setDiscountPrice(discount.getNum());
            }else{
                //满折
                BigDecimal p = price.multiply(discount.getNum());
                purchaseInfoVo.setDiscountPrice(price.subtract(p));
                purchaseStoreVo.setDiscountPrice(purchaseInfoVo.getDiscountPrice());
                price = p;
            }
        }else{
            purchaseInfoVo.setDiscountPrice(BigDecimal.valueOf(0));
        }
        purchaseInfoVo.setFinalPrice(price);
        purchaseInfoVo.setStoreList(storeInfoList);
        purchaseStoreVo.setFinalPrice(price);
        purchaseStoreVo.setDeliveryTimeOptions(new ArrayList<>());
        //添加配送时间
        LocalDate day = LocalDate.now();
        //判断当前是否可以选择今天配送
        if(LocalTime.now().isBefore(delivery.getDeliveryTime().minusHours(1)) //在今天配送前1小时下单可以今天配送
            && isNotRestDate(day, delivery.getRestMonth(), delivery.getRestDay(), delivery.getRestWeek()) //今天不是休息日可以配送
        ){
            purchaseStoreVo.getDeliveryTimeOptions().add(delivery.getDeliveryTime().atDate(day));
        }
        //添加活动天数内的配送选项
        Integer activeDays = delivery.getActiveDays();
        if (activeDays == null || activeDays < 3) activeDays = 3;
        for(int i = 0; i < activeDays; i++){
            day = day.plusDays(1);
            if(isNotRestDate(day, delivery.getRestMonth(), delivery.getRestDay(), delivery.getRestWeek()))
                purchaseStoreVo.getDeliveryTimeOptions().add(delivery.getDeliveryTime().atDate(day));
        }
        //当前时间
        purchaseInfoVo.setTime(LocalDateTime.now());




        return Result.success(purchaseInfoVo);
    }


    /**
     * 判断当天是否是商家的休息时间
     */
    private boolean isNotRestDate(LocalDate date, Integer restMonth, Long restDay, Integer restWeek){
        System.out.println(date.getDayOfWeek().getValue()+","+restWeek);
        return ((restMonth >> (date.getMonthValue() - 2)) & 1) == 0   //当月休息
                && ((restDay >> (date.getDayOfMonth() - 1)) & 1) == 0 //该日期休息
                && ((restWeek >> (date.getDayOfWeek().getValue()) - 1) & 1) == 0;  //该周天休息
    }
}
