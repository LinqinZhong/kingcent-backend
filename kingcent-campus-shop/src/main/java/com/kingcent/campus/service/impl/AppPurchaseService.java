package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.AddressService;
import com.kingcent.campus.service.OrderGoodsService;
import com.kingcent.campus.service.PurchaseService;
import com.kingcent.campus.shop.entity.*;
import com.kingcent.campus.shop.entity.vo.address.AddressVo;
import com.kingcent.campus.shop.entity.vo.purchase.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
@Slf4j
public class AppPurchaseService implements PurchaseService {

    @Autowired
    private OrderGoodsService orderGoodsService;
    @Autowired
    private AppGoodsSkuService skuService;
    @Autowired
    private AppShopService shopService;

    @Autowired
    private AppShopDeliveryTemplateService deliveryTemplateService;

    @Autowired
    private AppShopGoodsDiscountService goodsDiscountService;

    @Autowired
    private AppGoodsService goodsService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AppPayTypeService payTypeService;

    @Autowired
    private AppShopDeliveryGroupService deliveryGroupService;


    @Autowired
    private AppOrderService orderService;

    @Override
    public Result<PurchaseInfoVo> getPurchaseInfo(Long userId, CheckPurchaseVo check){

        long start = System.currentTimeMillis();

        //检查是否存在订单异常
        Result<PurchaseInfoVo> checkResult = orderService.checkOrder(userId, check.getList().size());
        if (checkResult != null) return checkResult;


        log.info("用时{}", System.currentTimeMillis() - start);

        //收集goodsId，count
        Set<Long> goodsIds = new HashSet<>();
        Map<String, Integer> countMap = new HashMap<>();
        for (QueryPurchaseVo query : check.getList()) {
            goodsIds.add(query.getGoodsId());
            countMap.put(query.getGoodsId()+"-"+query.getSpecInfo(), query.getCount());
        }


        //1.获取商品列表
        start = System.currentTimeMillis();
        List<GoodsEntity> goodsEntityList = goodsService.listByIds(goodsIds);
        if(goodsEntityList.size() == 0) return Result.fail("商品不存在");
        //提取数据
        Map<Long, GoodsEntity> goodsMap = new HashMap<>();
        for (GoodsEntity goods : goodsEntityList) {
            goodsMap.put(goods.getId(), goods);
        }
        log.info("用时{}", System.currentTimeMillis() - start);


        //收集shopId
        Set<Long> shopIds = new HashSet<>();
        for (GoodsEntity goods : goodsEntityList) {
            shopIds.add(goods.getShopId());
        }

        //2.获取商铺名称
        start = System.currentTimeMillis();
        Map<Long, String> shopNames = shopService.shopNamesMap(shopIds);
        log.info("用时{}", System.currentTimeMillis() - start);


        //3.获取商品规格列表
        start = System.currentTimeMillis();
        QueryWrapper<GoodsSkuEntity> skuWrapper = new QueryWrapper<>();
        //添加条件
        for (QueryPurchaseVo query : check.getList()) {
            skuWrapper.or(w->{
                w.eq("goods_id", query.getGoodsId());
                w.eq("spec_info", query.getSpecInfo());
            });
        }
        List<GoodsSkuEntity> skus = skuService.list(skuWrapper);

        //提取skuIds
        List<Long> skuIds = new ArrayList<>();
        for (GoodsSkuEntity sku : skus) {
            skuIds.add(sku.getId());
        }
        log.info("用时{}", System.currentTimeMillis() - start);

        //4.获取配送信息
        start = System.currentTimeMillis();
        List<DeliveryTemplateEntity> deliveries = deliveryTemplateService.list(
                new QueryWrapper<DeliveryTemplateEntity>()
                        .in("shop_id", shopIds)
                        .eq("is_used", 1)
        );
        log.info("用时{}", System.currentTimeMillis() - start);

        //5.获取地址信息
        start = System.currentTimeMillis();
        List<AddressVo> userAddress = addressService.getUserAddress(userId);
        log.info("用时{}", System.currentTimeMillis() - start);

        //6.获取支持的支付方式
        start = System.currentTimeMillis();
        List<PayTypeEntity> payTypes = payTypeService.list(
                new QueryWrapper<PayTypeEntity>()
                        .in("shop_id", shopIds)
                        .eq("enabled", true)
        );
        log.info("用时{}", System.currentTimeMillis() - start);

        //7.配送范围信息
        start = System.currentTimeMillis();
        Map<Long, DeliveryGroup> deliveryGroupMap = new HashMap<>();
        //用户有设置地址才能查询配送范围信息
        AddressVo defaultAddress = null;
        if(userAddress.size() > 0){
            if(check.getAddressId() != null){
                //用户指定了地址，使用指定地址
                for (AddressVo address : userAddress) {
                    if (Objects.equals(address.getId(), check.getAddressId())) {
                        defaultAddress = address;
                        break;
                    }
                }
            }else {
                //用户没有指定地址，从收货地址中找默认地址
                for (AddressVo address : userAddress) {
                    if (address.getIsDefault()) {
                        defaultAddress = address;
                        break;
                    }
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

        log.info("用时{}", System.currentTimeMillis() - start);
        start = System.currentTimeMillis();


        //查询用户对sku购买次数
        Map<Long, Integer> skuBuyCount = orderGoodsService.countUserBuyCountOfSku(userId,skuIds);
        log.info("用时{}", System.currentTimeMillis() - start);


        start = System.currentTimeMillis();
        //整合数据
        //交易信息
        PurchaseInfoVo purchaseInfoVo = new PurchaseInfoVo();
        //当前系统时间（用于前后端统一时间）
        purchaseInfoVo.setTime(LocalDateTime.now());
        //用户收货地址
        purchaseInfoVo.setAddressList(userAddress);
        //当前使用的地址id
        if(defaultAddress != null) purchaseInfoVo.setAddressId(defaultAddress.getId());
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
            if(LocalTime.now()
                    //在今天配送预留时间前下单可以今天配送
                    .isBefore(delivery.getDeliveryTime()
                            .minusMinutes(
                                    //预留时间是给用户备货的
                                    // 加上30分钟，是预留出来给用户支付的
                                    delivery.getReserveTime()+30
                            )
                    )
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
            PurchaseGoodsVo vo = new PurchaseGoodsVo();
            vo.setStock(sku.getSafeStockQuantity());
            vo.setCountBought(skuBuyCount.getOrDefault(sku.getId(), 0));
            vo.setSku(sku.getSpecInfo());
            vo.setSkuDesc(sku.getDescription());
            vo.setPrice(sku.getPrice());
            vo.setThumbnail(sku.getImage());
            vo.setId(goods.getId());
            vo.setTitle(goods.getName());
            //获取商铺对象
            PurchaseStoreVo store = storeMap.get(goods.getShopId());
            store.setPrice(BigDecimal.valueOf(0));
            store.setDiscountPrice(BigDecimal.valueOf(0));
            //获取商品数量
            Integer count = countMap.get(sku.getGoodsId()+"-"+sku.getSpecInfo());
            if (count == null) continue;

            //商品库存大于0时才计算费用
            if(sku.getSafeStockQuantity() > 0) {


                //商品限购
                if(sku.getLimitMinCount() != null && count < sku.getLimitMinCount()) {
                    //最低限制
                    return Result.fail("数量不能少于起购数量，请重试");
                }else if(sku.getLimitMaxCount() != null){
                    //最高限制
                    if(count > sku.getLimitMaxCount()){
                        return Result.fail("数量不能超过限购数量");
                    }else if(count + skuBuyCount.getOrDefault(sku.getId(), 0) > sku.getLimitMaxCount()){
                        return Result.fail("您此前已经买过此商品，请确保累计购买没有超过限购数量");
                    }
                }

                //调整数量
                if(sku.getSafeStockQuantity() < count){
                    if(sku.getLimitMinCount()  != null && sku.getSafeStockQuantity() < sku.getLimitMinCount()){
                        //剩余库存少于起购件数，判断为库存不足
                        return Result.fail("商品库存不足");
                    }
                    //库存不足数量时，将数量设置为最大值
                    count = sku.getSafeStockQuantity();
                    //提示用户数量变动了
                    vo.setCountIsReset(true);
                }else{
                    vo.setCountIsReset(false);
                }



                //折扣信息
                GoodsDiscountEntity discount = goodsDiscountService.getBestDiscount(sku.getGoodsId(), count);
                //价格和优惠金额
                BigDecimal price = sku.getPrice().multiply(BigDecimal.valueOf(count));
                if (discount != null) {
                    if (discount.getType() == 1) {
                        //满减
                        price = price.subtract(discount.getNum());
                        store.setDiscountPrice(store.getDiscountPrice().add(discount.getNum()));
                    } else {
                        //满折
                        BigDecimal p = price.multiply(discount.getNum());
                        store.setDiscountPrice(store.getDiscountPrice().add(price.subtract(p)));
                        price = p;
                    }

                    purchaseInfoVo.setDiscountPrice(store.getDiscountPrice());
                } else {
                    purchaseInfoVo.setDiscountPrice(BigDecimal.valueOf(0));
                }
                store.setPrice(price);

                //运费
                if (deliveryGroupMap.containsKey(store.getId())) {
                    BigDecimal deliveryFee = deliveryGroupMap.get(store.getId()).getDeliveryFee();
                    store.setPrice(store.getPrice().add(deliveryFee));
                    store.setDeliveryFee(deliveryFee);
                }
            }


            vo.setCount(count);
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
        log.info("用时{}", System.currentTimeMillis() - start);
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
