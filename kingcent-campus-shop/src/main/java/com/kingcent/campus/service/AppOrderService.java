package com.kingcent.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.*;
import com.kingcent.campus.shop.entity.vo.order.CreateOrderResultVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmGoodsVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmStoreVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.mapper.OrderMapper;
import com.kingcent.campus.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class AppOrderService extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AppShopDeliveryGroupService deliveryGroupService;

    @Autowired
    private AppGoodsSkuService goodsSkuService;

    @Autowired
    private AppShopDeliveryTemplateService deliveryTemplateService;

    @Autowired
    private AppShopGoodsDiscountService goodsDiscountService;

    @Autowired
    private AppOrderGoodsService orderGoodsService;

    @Autowired
    private AppPayTypeService payTypeService;

    /**
     * 检查用户操作是否存在订单异常，存在则返回错误，不存在则返回null
     * @param userId 用户id
     * @param orderNum 请求订单的数量
     */
    @Override
    public <T> Result<T> checkOrder(Long userId, Integer orderNum, Class<T> tClass){
        //查询用户是否存在未支付的线上支付订单，有则拦截
        if(count(
                new QueryWrapper<OrderEntity>()
                        .eq("user_id", userId)
                        .ne("pay_type", "offline")
                        .eq("status",0)
                        .last("limit 1")
        ) > 0){
            return Result.fail("您当前有待支付的订单，请完成订单后再下单", tClass);
        }

        //累计未完成的到付订单数量不能超过15个
        if(count(
                new QueryWrapper<OrderEntity>()
                        .eq("user_id", userId)
                        .eq("pay_type", "offline")
                        .eq("status",0)
        ) + orderNum > 15){
            return Result.fail("到付订单数量过多，请勿超过15个", tClass);
        }
        return null;
    }

    /**
     * 创建订单
     */
    @Override
    @Transactional
    public Result<CreateOrderResultVo> createOrders(Long userId, Long loginId, PurchaseConfirmVo purchase){

        //检查是否存在订单异常
        Result<CreateOrderResultVo> checkResult = checkOrder(userId, purchase.getStoreList().size(), CreateOrderResultVo.class);
        if (checkResult != null) return checkResult;

        CreateOrderResultVo result = new CreateOrderResultVo();

        //查询用户收货地址
        AddressEntity address = addressService.getById(purchase.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) return Result.fail("收货地址不存在", CreateOrderResultVo.class);

        //所有在线支付的金额
        BigDecimal onlinePayPrice = new BigDecimal(0);

        //SKU
        Map<String,GoodsSkuEntity> skuMap = new HashMap<>();
        //配送模板
        Map<Long, DeliveryTemplateEntity> deliveryTmpMap = new HashMap<>();
        //配送范围
        Map<Long, DeliveryGroup> deliveryGroupMap = new HashMap<>();
        //支付方式
        Map<Long, String> payTypeMaps = new HashMap<>();
        //skuIds
        List<Long> skuIds = new ArrayList<>();

        //---------------------------------------------------------------------
        //提取数据
        //商店id集合
        List<Long> shopIds = new ArrayList<>();
        //收集配送时间
        Map<Long, LocalDateTime> deliveryTimes = new HashMap<>();
        //查询sku的条件式
        QueryWrapper<GoodsSkuEntity> skuWrapper = new QueryWrapper<>();
        //查询payType的条件式
        QueryWrapper<PayTypeEntity> payTypeWrapper = new QueryWrapper<>();
        for (PurchaseConfirmStoreVo store : purchase.getStoreList()) {
            shopIds.add(store.getId());
            payTypeWrapper.or(w->{
                w.eq("shop_id", store.getId());
                w.eq("type", store.getPayType());
            });
            deliveryTimes.put(store.getId(), store.getDeliveryTime());
            for (PurchaseConfirmGoodsVo goods : store.getGoodsList()) {
                skuWrapper.or(w->{
                    w.eq("goods_id", goods.getId());
                    w.eq("spec_info", goods.getSku());
                });
            }
        }
        //---------------------------------------------------------------------

        //---------------------------------------------------------------------
        //查询sku
        List<GoodsSkuEntity> skus = goodsSkuService.list(skuWrapper);
        if(skus.size() == 0) return Result.fail("商品不存在", CreateOrderResultVo.class);
        for (GoodsSkuEntity goodsSkuEntity : skus) {
            skuMap.put(goodsSkuEntity.getGoodsId()+"-"+goodsSkuEntity.getSpecInfo(), goodsSkuEntity);
            skuIds.add(goodsSkuEntity.getId());
        }
        //--------------------------------------------------------------------


        //查询用户对sku购买次数
        Map<Long, Integer> skuBuyCount = orderGoodsService.countUserBuyCountOfSku(userId,skuIds);


        //--------------------------------------------------------------------
        //查询配送模板
        List<DeliveryTemplateEntity> deliveryTemps = deliveryTemplateService.list(
                new QueryWrapper<DeliveryTemplateEntity>()
                        .in("shop_id", shopIds)
                        .eq("is_used", 1)
        );
        for (DeliveryTemplateEntity delivery : deliveryTemps) {
            deliveryTmpMap.put(delivery.getShopId(), delivery);
        }
        //检查是否能够配送
        for (Long shopId : shopIds) {
            if(!deliveryTmpMap.containsKey(shopId)){
                return Result.fail("商家已暂停营业", CreateOrderResultVo.class);
            }
            DeliveryTemplateEntity delivery = deliveryTmpMap.get(shopId);
            LocalDateTime deliveryTime = deliveryTimes.get(shopId);
            LocalDate deliveryDate = deliveryTime.toLocalDate();
            if(deliveryTime.toLocalDate().equals(LocalDate.now()) && LocalDateTime.now().isAfter(deliveryTime.minusMinutes(delivery.getReserveTime()))){
                return Result.fail("配送时间在商家的预留时间内，请更换配送时间", CreateOrderResultVo.class);
            }
            if (!isNotRestDate(deliveryDate, delivery.getRestMonth(), delivery.getRestDay(), delivery.getRestWeek())) {
                return Result.fail("商家当天休息，请更换配送时间", CreateOrderResultVo.class);
            }
        }

        //--------------------------------------------------------------------

        //--------------------------------------------------------------------
        //查询配送范围
        List<DeliveryGroup> deliveryGroups = deliveryGroupService.list(
                new QueryWrapper<DeliveryGroup>()
                        .eq("group_id", address.getGroupId())
                        .in("shop_id", shopIds)
        );
        for(DeliveryGroup deliveryGroup : deliveryGroups){
            deliveryGroupMap.put(deliveryGroup.getShopId(), deliveryGroup);
        }
        for (Long shopId : shopIds) {
            if(!deliveryGroupMap.containsKey(shopId)){
                return Result.fail("当前地址超出配送范围", CreateOrderResultVo.class);
            }
        }
        //--------------------------------------------------------------------

        //--------------------------------------------------------------------
        //查询店铺支持的支付方式
        List<PayTypeEntity> payTypes = payTypeService.list(payTypeWrapper);
        for (PayTypeEntity payType : payTypes) {
            payTypeMaps.put(payType.getShopId(), payType.getType());
        }
        for (Long shopId : shopIds) {
            if (!payTypeMaps.containsKey(shopId)){
                return Result.fail("不支持该支付方式，请稍后重试", CreateOrderResultVo.class);
            }
        }
        //--------------------------------------------------------------------

        //按商铺生成订单
        List<OrderEntity> orders = new ArrayList<>();
        Map<String, List<OrderGoodsEntity>> orderGoodsListMap = new HashMap<>();
        for (PurchaseConfirmStoreVo store : purchase.getStoreList()) {

            OrderEntity order = new OrderEntity();
            BigDecimal orderPrice = new BigDecimal(0);
            BigDecimal orderDiscount = new BigDecimal(0);

            List<OrderGoodsEntity> orderGoodsList = new ArrayList<>();

            for (PurchaseConfirmGoodsVo goods : store.getGoodsList()) {

                //获取sku
                GoodsSkuEntity sku = skuMap.get(goods.getId()+"-"+goods.getSku());
                if(sku == null){
                    return Result.fail("商品不存在", CreateOrderResultVo.class);
                }

                //商品限购
                if(
                        (sku.getLimitMinCount() != null && goods.getCount() < sku.getLimitMinCount())   //最低限制
                                ||
                                sku.getLimitMaxCount() != null && goods.getCount() + (
                                        skuBuyCount.getOrDefault(sku.getId(), 0)            //最高限制
                                ) > sku.getLimitMaxCount()
                ){
                    //限制最低购买
                    return Result.fail("数量不符合购买要求，请重试", CreateOrderResultVo.class);
                }

                OrderGoodsEntity orderGoods = new OrderGoodsEntity();
                orderGoods.setCount(goods.getCount());
                orderGoods.setSkuId(sku.getId());
                orderGoods.setUnitPrice(sku.getPrice());
                orderGoods.setPrice(BigDecimal.valueOf(0));
                orderGoods.setDiscount(BigDecimal.valueOf(0));
                orderGoods.setUserId(userId);

                //更新库存
                if (goodsSkuService.update(
                        new UpdateWrapper<GoodsSkuEntity>()
                                .setSql("safe_stock_quantity = safe_stock_quantity - "+goods.getCount())
                                .eq("goods_id", goods.getId())
                                .eq("spec_info", goods.getSku())
                                .ge("safe_stock_quantity", goods.getCount()
                                )
                )) {
                    //计算价格
                    BigDecimal price = sku.getPrice().multiply(new BigDecimal(goods.getCount()));

                    //优惠金额
                    GoodsDiscountEntity discount = goodsDiscountService.getBestDiscount(goods.getId(), goods.getCount());
                    if(discount != null){
                        if (discount.getType() == 1){
                            //满减
                            price = price.subtract(discount.getNum());
                            orderGoods.setDiscount(discount.getNum());
                            orderDiscount = orderDiscount.add(discount.getNum());
                        }else{
                            //满折
                            BigDecimal p = price.multiply(discount.getNum());
                            orderGoods.setDiscount(price.subtract(p));
                            orderDiscount = orderDiscount.add(orderGoods.getDiscount());
                            price = p;
                        }
                    }
                    orderGoods.setPrice(price);
                    orderPrice = orderPrice.add(price);
                }else{
                    return Result.fail("商品库存不足，请重试",CreateOrderResultVo.class);
                }
                orderGoodsList.add(orderGoods);
            }


            orders.add(order);
            order.setOrderNo(createOrderNo(userId, loginId));
            order.setCreateTime(LocalDateTime.now());
            order.setDeliveryTime(deliveryTimes.get(store.getId()));
            order.setDeliveryFee(deliveryGroupMap.get(store.getId()).getDeliveryFee());
            orderPrice = orderPrice.add(order.getDeliveryFee());
            order.setReceiverName(address.getName());
            order.setReceiverMobile(address.getMobile());
            order.setReceiverGender(address.getGender());
            order.setPointId(address.getPointId());
            order.setUserId(userId);
            order.setStatus(0);
            order.setShopId(store.getId());
            order.setPayType(payTypeMaps.get(store.getId()));
            order.setRemark(store.getRemark());
            order.setPrice(orderPrice);
            order.setDiscount(orderDiscount);
            orderGoodsListMap.put(order.getOrderNo(), orderGoodsList);

            if (!order.getPayType().equals("offline")) {
                onlinePayPrice = onlinePayPrice.add(orderPrice);
            }
        }

        //创建订单
        if(saveBatch(orders)){
            List<Long> orderIds = new ArrayList<>();
            List<OrderGoodsEntity> orderGoodsEntities = new ArrayList<>();
            for (OrderEntity order : orders) {
                orderIds.add(order.getId());
                List<OrderGoodsEntity> orderGoods = orderGoodsListMap.get(order.getOrderNo());
                for (OrderGoodsEntity orderGood : orderGoods) {
                    orderGood.setOrderId(order.getId());
                    orderGoodsEntities.add(orderGood);
                }
            }
            result.setOrderIds(orderIds);
            orderGoodsService.saveBatch(orderGoodsEntities);
        }


        //是否需要线上支付
        result.setNeedPay(onlinePayPrice.doubleValue() > 0);

        return Result.success(result);
    }

    /**
     * 生成订单号
     */
    private String createOrderNo(Long userId, Long loginId){
        Long localDate = System.currentTimeMillis()+1708579437885L;
        return String.format("%7d%04d%04d",localDate, userId, loginId);
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
