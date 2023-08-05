package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.common.entity.*;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.AddressVo;
import com.kingcent.campus.entity.vo.purchase.PurchaseGoodsVo;
import com.kingcent.campus.entity.vo.purchase.PurchaseInfoVo;
import com.kingcent.campus.entity.vo.purchase.PurchaseStoreVo;
import com.kingcent.campus.entity.vo.purchase.QueryPurchaseVo;
import com.kingcent.campus.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    @Autowired
    private GoodsSkuService skuService;
    @Autowired
    private ShopService shopService;

    @Autowired
    private DeliveryTemplateService deliveryTemplateService;

    @Autowired
    private GoodsDiscountService goodsDiscountService;
    
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private AddressService addressService;
    
    @Autowired
    private PayTypeService payTypeService;

    @Autowired
    private DeliveryGroupService deliveryGroupService;

    @Override
    public Result<PurchaseInfoVo> getPurchaseInfo(Long userId, List<QueryPurchaseVo> queries){

        //收集goodsId，count
        Set<Long> goodsIds = new HashSet<>();
        Map<String, Integer> countMap = new HashMap<>();
        for (QueryPurchaseVo query : queries) {
            goodsIds.add(query.getGoodsId());
            countMap.put(query.getGoodsId()+"-"+query.getSpecInfo(), query.getCount());
        }

        //1.获取商品列表
        List<GoodsEntity> goodsEntityList = goodsService.listByIds(goodsIds);
        if(goodsEntityList.size() == 0) return Result.fail("商品不存在", PurchaseInfoVo.class);
        //提取数据
        Map<Long, GoodsEntity> goodsMap = new HashMap<>();
        for (GoodsEntity goods : goodsEntityList) {
            goodsMap.put(goods.getId(), goods);
        }

        //收集shopId
        Set<Long> shopIds = new HashSet<>();
        for (GoodsEntity goods : goodsEntityList) {
            shopIds.add(goods.getShopId());
        }

        //2.获取商铺名称
        Map<Long, String> shopNames = shopService.shopNamesMap(shopIds);

        //3.获取商品规格列表
        QueryWrapper<GoodsSkuEntity> skuWrapper = new QueryWrapper<>();
        //添加条件
        for (QueryPurchaseVo query : queries) {
            skuWrapper.or(w->{
                w.eq("goods_id", query.getGoodsId());
                w.eq("spec_info", query.getSpecInfo());
            });
        }
        List<GoodsSkuEntity> skus = skuService.list(skuWrapper);

        //4.获取商品折扣信息
        QueryWrapper<GoodsDiscountEntity> discountWrapper = new QueryWrapper<>();
        for (QueryPurchaseVo query : queries) {
            discountWrapper.or(w->{
                w.eq("goods_id", query.getGoodsId());
                w.le("more_than", query.getCount());
            });
        }
        discountWrapper
                .select("MIN(more_than) AS more_than, goods_id, type, num")
                .groupBy("goods_id");
        List<GoodsDiscountEntity> discounts = goodsDiscountService.list(discountWrapper);
        //提取数据
        Map<Long, GoodsDiscountEntity> discountMap = new HashMap<>();
        for (GoodsDiscountEntity discount : discounts) {
            discountMap.put(discount.getGoodsId(), discount);
        }

        //5.获取配送信息
        List<DeliveryTemplateEntity> deliveries = deliveryTemplateService.list(
                new QueryWrapper<DeliveryTemplateEntity>()
                        .in("shop_id", shopIds)
                        .eq("is_used", 1)
        );

        //6.获取地址信息
        List<AddressVo> userAddress = addressService.getUserAddress(userId);

        //7.获取支持的支付方式
        List<PayTypeEntity> payTypes = payTypeService.list(
                new QueryWrapper<PayTypeEntity>()
                        .in("shop_id", shopIds)
                        .eq("enabled", true)
        );

        //8.配送范围信息
        Map<Long, DeliveryGroup> deliveryGroupMap = new HashMap<>();
        //用户有设置地址才能查询配送范围信息
        if(userAddress.size() > 0){
            //从收货地址中找默认地址
            AddressVo defaultAddress = null;
            for (AddressVo address : userAddress) {
                if (address.getIsDefault()){
                    defaultAddress = address;
                    break;
                }
            }
            //没有默认地址，使用第一个地址
            if(defaultAddress == null) defaultAddress = userAddress.get(0);
            QueryWrapper<DeliveryGroup> deliveryGroupWrapper = new QueryWrapper<>();
            deliveryGroupWrapper.in("shop_id", shopIds);
            deliveryGroupWrapper.eq("group_id", defaultAddress.getGroupId());
            List<DeliveryGroup> list = deliveryGroupService.list(deliveryGroupWrapper);
            for (DeliveryGroup deliveryGroup : list) {
                deliveryGroupMap.put(deliveryGroup.getShopId(), deliveryGroup);
            }
        }


        //整合数据
        //交易信息
        PurchaseInfoVo purchaseInfoVo = new PurchaseInfoVo();
        //当前系统时间（用于前后端统一时间）
        purchaseInfoVo.setTime(LocalDateTime.now());
        //用户收货地址
        purchaseInfoVo.setAddressList(userAddress);
        purchaseInfoVo.setStoreList(new ArrayList<>());
        purchaseInfoVo.setDiscountPrice(BigDecimal.valueOf(0));
        Map<Long, PurchaseStoreVo> storeMap = new HashMap<>();
        //店铺信息
        for (Long shopId : shopIds) {
            PurchaseStoreVo store = new PurchaseStoreVo();
            //Id
            store.setId(shopId);
            //店铺名称
            store.setName(shopNames.get(shopId));
            purchaseInfoVo.getStoreList().add(store);
            store.setDeliveryTimeOptions(new ArrayList<>());
            store.setGoodsList(new ArrayList<>());
            store.setPayTypes(new ArrayList<>());
            storeMap.put(shopId, store);
        }
        //配送时间
        for (DeliveryTemplateEntity delivery : deliveries) {
            //获取商品对象
            PurchaseStoreVo store = storeMap.get(delivery.getShopId());
            if(store == null) continue;
            //判断当前是否可以选择今天配送
            LocalDate date = LocalDate.now();
            if(LocalTime.now().isBefore(delivery.getDeliveryTime().minusMinutes(delivery.getReserveTime())) //在今天配送预留时间前下单可以今天配送
                    && isNotRestDate(date, delivery.getRestMonth(), delivery.getRestDay(), delivery.getRestWeek()) //今天不是休息日可以配送
            ){
                store.getDeliveryTimeOptions().add(delivery.getDeliveryTime().atDate(date));
            }
            //添加活动天数内的配送选项
            Integer activeDays = delivery.getActiveDays();
            if (activeDays == null || activeDays < 3) activeDays = 3;
            for(int i = 0; i < activeDays; i++){
                date = date.plusDays(1);
                if(isNotRestDate(date, delivery.getRestMonth(), delivery.getRestDay(), delivery.getRestWeek()))
                    store.getDeliveryTimeOptions().add(delivery.getDeliveryTime().atDate(date));
            }
        }
        //以sku为单位生成商品列表
        for (GoodsSkuEntity sku : skus) {
            //获取商品对象
            GoodsEntity goods = goodsMap.get(sku.getGoodsId());
            //获取商铺对象
            PurchaseStoreVo store = storeMap.get(goods.getShopId());
            store.setPrice(BigDecimal.valueOf(0));
            store.setDiscountPrice(BigDecimal.valueOf(0));
            //获取商品数量
            Integer count = countMap.get(sku.getGoodsId()+"-"+sku.getSpecInfo());
            //折扣信息
            GoodsDiscountEntity discount = discountMap.get(goods.getId());

            if (count == null) continue;

            PurchaseGoodsVo vo = new PurchaseGoodsVo();
            vo.setCount(count);
            vo.setSkuDesc(sku.getDescription());
            vo.setPrice(sku.getPrice());
            vo.setThumbnail(sku.getImage());
            vo.setId(goods.getId());
            vo.setTitle(goods.getName());

            //价格和优惠金额
            BigDecimal price = sku.getPrice().multiply(BigDecimal.valueOf(count));
            if(discount != null){
                if (discount.getType() == 1){
                    //满减
                    price = price.subtract(discount.getNum());
                    store.setDiscountPrice(store.getDiscountPrice().add(discount.getNum()));
                }else{
                    //满折
                    BigDecimal p = price.multiply(discount.getNum());
                    store.setDiscountPrice(store.getDiscountPrice().add(price.subtract(p)));
                    price = p;
                }

                purchaseInfoVo.setDiscountPrice(store.getDiscountPrice());
            }else{
                purchaseInfoVo.setDiscountPrice(BigDecimal.valueOf(0));
            }
            store.setPrice(price);

            //运费
            if(deliveryGroupMap.containsKey(store.getId())){
                BigDecimal deliveryFee = deliveryGroupMap.get(store.getId()).getDeliveryFee();
                store.setPrice(store.getPrice().add(deliveryFee));
                store.setDeliveryFee(deliveryFee);
            }
            store.getGoodsList().add(vo);
        }

        //支付方式
        for (PayTypeEntity payType : payTypes) {
            //获取店铺
            PurchaseStoreVo store = storeMap.get(payType.getShopId());
            if (store != null){
                store.getPayTypes().add(payType.getType());
            }
        }

        return Result.success(purchaseInfoVo);
    }


    /**
     * 判断当天是否是商家的休息时间
     */
    private boolean isNotRestDate(LocalDate date, Integer restMonth, Long restDay, Integer restWeek){
        return ((restMonth >> (date.getMonthValue() - 2)) & 1) == 0   //当月休息
                && ((restDay >> (date.getDayOfMonth() - 1)) & 1) == 0 //该日期休息
                && ((restWeek >> (date.getDayOfWeek().getValue()) - 1) & 1) == 0;  //该周天休息
    }
}
