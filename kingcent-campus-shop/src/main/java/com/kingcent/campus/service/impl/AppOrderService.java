package com.kingcent.campus.service.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.service.*;
import com.kingcent.campus.shop.constant.*;
import com.kingcent.campus.shop.entity.*;
import com.kingcent.campus.wx.entity.WxOrderGoodsEntity;
import com.kingcent.campus.wx.entity.vo.CreateWxOrderResultVo;
import com.kingcent.campus.shop.entity.vo.order.OrderGoodsVo;
import com.kingcent.campus.shop.entity.vo.order.OrderStoreVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmGoodsVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmStoreVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.mapper.OrderMapper;
import com.kingcent.campus.wx.entity.vo.WxPaymentInfoVo;
import com.kingcent.campus.wx.service.WxOrderService;
import com.kingcent.campus.wx.service.WxPayService;
import com.kingcent.campus.wx.service.WxRefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
@Slf4j
public class AppOrderService extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService, WxOrderService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private DeliveryGroupService deliveryGroupService;

    @Autowired
    private GoodsSkuService goodsSkuService;

    @Autowired
    private DeliveryTemplateService deliveryTemplateService;

    @Autowired
    private GoodsDiscountService goodsDiscountService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private GroupPointService pointService;

    @Autowired
    private WxPayService wxPayService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AppUserService userService;

    @Autowired
    private OrderRefundService refundService;

    @Autowired
    private OrderRefundMapService refundMapService;

    @Autowired
    private WxRefundService wxRefundService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    //自动关单消息队列键
    private static final String MESSAGE_KEY = "message:queue:order:dead";

    //取货码键
    private static final String RECEIVE_CODE_KEY = "receive:code";

    @Autowired
    private CarrierService carrierService;

    @Autowired
    private OrderDeliveryService orderDeliveryService;


    @Autowired
    private CartGoodsService cartGoodsService;


    //订单超时自动关闭暂时使用redis作为消息队列，后期可能会使用其它mq中间件
    //---------------------------------------------------------------------------

    /**
     * 关闭过期订单
     */
    @Override
    public void closeDeadOrder(){
        ZSetOperations<String, String> ops = redisTemplate.opsForZSet();
        long current = System.currentTimeMillis() + 1000;
        //一次处理10条
        Set<String> set = ops.rangeByScore(
                MESSAGE_KEY,
                0,
                current,
                0,
                10
        );
        if (set == null || set.size() == 0) return;
        removeCloseOrderTask(set);
        //删除
        List<Long> orderIds = new ArrayList<>();
        for (String s : set) {
            orderIds.add(Long.valueOf(s));
        }
        if (closeOrder(orderIds)){
            log.info("自动关单成功,{}", set);
        }
        for (String s : set) {
            ops.remove(MESSAGE_KEY, s);
        }
    }

    /**
     * 为订单设置自动关闭任务
     * @param orderId 订单id
     * @param deadline 过期时间
     */
    private boolean setOrderAutoCloseTask(Long orderId, long deadline){
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(
                MESSAGE_KEY,
                orderId+"",
                deadline
        ));
    }

    /**
     * 移除自动关闭订单任务
     * @param orderIds 订单id
     */
    @Override
    public boolean removeCloseOrderTask(Set<String> orderIds){
        ZSetOperations<String, String> ops = redisTemplate.opsForZSet();
        String[] ids = orderIds.toArray(new String[0]);
        ops.remove(MESSAGE_KEY, ids);
        return true;
    }
    //---------------------------------------------------------------------------


    /**
     * 关闭订单
     * @param orderIds 订单id
     */
    @Override
    @Transactional
    public boolean closeOrder(Collection<Long> orderIds){
        //过滤出可以关闭的订单
        List<OrderEntity> orderUnclose = list(
                new QueryWrapper<OrderEntity>()
                        .in("id", orderIds)
                        .and(w0->{
                            w0.or(w->w.eq("status", OrderStatus.NOT_PAY));
                            w0.or(w->{
                                w.eq("pay_type", PayType.OFFLINE);
                                w.eq("status", OrderStatus.READY);
                            });
                        })
                        .select("id")
        );
        orderIds = new ArrayList<>();
        for (OrderEntity order : orderUnclose) {
            orderIds.add(order.getId());
        }

        if(orderIds.size() == 0){
            return false;
        }

        //关闭订单
        boolean update = update(new UpdateWrapper<OrderEntity>()
                .in("id", orderIds)
                .set("status", -1)
                .set("finish_time", LocalDateTime.now())
        );

        if(update){
            //库存回滚
            List<OrderGoodsEntity> goodsList = orderGoodsService.list(
                    new QueryWrapper<OrderGoodsEntity>()
                            .in("order_id", orderIds)
                            .select("sku_id, count")
            );
            if (goodsList.size() == 0) return true; //商品为空？一般不会出现这种情况
            for (OrderGoodsEntity goods : goodsList) {
                if(!goodsSkuService.changeSafeStockQuantity(goods.getSkuId(), goods.getCount())) {
                    //如果存在这个sku，则修改失败
                    //如果sku不存在，修改失败归为正常情况
                    if(goodsSkuService.getById(goods.getSkuId()) != null){
                        log.error("库存回滚失败：{}", goods.getSkuId());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public VoList<OrderStoreVo> orderList(Long userId, Integer status, Integer page) {
        Page<OrderEntity> pager = new Page<>(page, 5, true);
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("id");
        if(status != null) wrapper.eq("status", status);


        Page<OrderEntity> res = page(pager, wrapper);

        //没有数据
        if (res.getRecords().size() == 0){
            return new VoList<>((int) res.getTotal(),new ArrayList<>());
        }

        List<OrderStoreVo> orders = new ArrayList<>();
        List<Long> orderIds = new ArrayList<>();

        Map<Long, OrderStoreVo> map = new HashMap<>();

        //查询商铺名称
        Set<Long> shopIds = new HashSet<>();
        for (OrderEntity record : res.getRecords()) {
            shopIds.add(record.getShopId());
        }
        Map<Long, String> shopNames = shopService.shopNamesMap(shopIds);


        for (OrderEntity order : res.getRecords()) {
            OrderStoreVo o = new OrderStoreVo();
            o.setPayPrice(order.getPayPrice());
            o.setStatus(order.getStatus());
            o.setCreateTime(order.getCreateTime());
            shopIds.add(order.getShopId());
            o.setShopName(shopNames.getOrDefault(order.getShopId(),"店铺"+order.getShopId()));
            orderIds.add(order.getId());
            o.setPayType(order.getPayType());
            o.setOrderId(order.getId());
            o.setTradeNo(order.getTradeNo());
            o.setGoodsList(new ArrayList<>());
            o.setPaymentDeadline(order.getPaymentDeadline());
            orders.add(o);
            map.put(order.getId(), o);
        }

        //查询商品
        List<OrderGoodsEntity> orderGoodsList = orderGoodsService.list(
                new QueryWrapper<OrderGoodsEntity>()
                        .eq("user_id", userId)
                        .in("order_id", orderIds)
        );
        for (OrderGoodsEntity goods : orderGoodsList) {
            OrderStoreVo order = map.get(goods.getOrderId());
            List<OrderGoodsVo> goodsList = order.getGoodsList();
            OrderGoodsVo orderGoodsVo = new OrderGoodsVo();
            orderGoodsVo.setPrice(goods.getPrice());
            orderGoodsVo.setTitle(goods.getTitle());
            orderGoodsVo.setThumb(goods.getThumbnail());
            orderGoodsVo.setCount(goods.getCount());
            orderGoodsVo.setSkuInfo(goods.getSkuInfo());
            goodsList.add(orderGoodsVo);
        }
        return new VoList<>((int) pager.getTotal(),orders);
    }

    /**
     * 检查用户操作是否存在订单异常，存在则返回错误，不存在则返回null
     * @param userId 用户id
     * @param orderNum 请求订单的数量
     */
    @Override
    public <T> Result<T> checkOrder(Long userId, Integer orderNum){
        //查询用户是否存在未支付的线上支付订单，有则拦截
        if(count(
                new QueryWrapper<OrderEntity>()
                        .eq("user_id", userId)
                        .ne("pay_type", PayType.OFFLINE)
                        .eq("status",0)
                        .last("limit 1")
        ) > 0){
            return Result.fail("您当前有待支付的订单，请完成订单后再下单");
        }

        //累计未完成的到付订单数量不能超过15个
        if(count(
                new QueryWrapper<OrderEntity>()
                        .eq("user_id", userId)
                        .eq("pay_type", PayType.OFFLINE)
                        .eq("status",0)
        ) + orderNum > 15){
            return Result.fail("到付订单数量过多，请勿超过15个");
        }
        return null;
    }

    /**
     * 订单下单时未支付重新发起的支付请求
     * @param userId 用户ID
     * @param loginId 登录ID
     * @param orderId 订单ID
     * @return 微信支付报文
     */
    @Override
    public Result<?> pay(Long userId, Long loginId, Long orderId, String ipAddress){
        //获取订单
        OrderEntity order = getById(orderId);
        if(order == null || !order.getUserId().equals(userId))
            return Result.fail("订单不存在");
        if(order.getPayType().equals(PayType.OFFLINE)){
            return Result.fail("到付订单，不可在线支付");
        }
        switch (order.getStatus()) {
            case OrderStatus.READY:
            case OrderStatus.DELIVERING:
            case OrderStatus.REVIEWED:
            case OrderStatus.ARRIVED:
                return Result.fail("订单已支付，请勿重复支付");
            case OrderStatus.CLOSED:
                return Result.fail("订单已关闭");
            case OrderStatus.NOT_PAY:{
                //处理微信支付
                if(order.getPayType().equals(PayType.WX_PAY)) {
                    //查询订单是否存在未关闭的支付订单
                    if(order.getPaymentId() != null) {
                        OrderPaymentEntity payment = orderPaymentService.getById(order.getPaymentId());
                        JSONObject check = wxPayService.checkOrder(order.getOrderNo());
                        if (check == null || !check.containsKey("trade_state"))
                            return Result.fail("订单校验失败，请稍后重试");
                        switch (check.getString("trade_state")) {
                            case "SUCCESS":
                            case "REFUND":
                                return Result.fail("订单已支付，请勿重复支付（如果仍显示未支付，请联系管理员处理）");
                            case "CLOSED":
                                return Result.fail("订单已关闭");
                            case "NOTPAY": {
                                //订单关闭，重新发起支付
                                if(payment == null) return repay(order,userId,ipAddress, false);
                                //该批支付包含其它订单，关闭后重新发起支付
                                if(payment.getOrderTotal() > 1) return repay(order,userId,ipAddress, true);
                            }
                        }
                        //返回原来的支付报文
                        return Result.success(JSONObject.parseObject(payment.getPaymentPackage(), WxPaymentInfoVo.class));
                    }
                    //订单之前没有未关闭的支付订单，发起支付
                    return repay(order,userId,ipAddress, false);
                }

            }
        }

        return Result.fail("接口错误，请联系管理员");
    }

    /**
     * 重新拉取支付信息
     * @param order 订单给
     * @param userId 用户id
     * @param ipAddress ip地址
     * @param needClose 是否需要先关闭原来的订单
     */
    @Transactional
    private Result<?> repay(OrderEntity order,Long userId, String ipAddress, boolean needClose){
        String openId = userService.getWxOpenid(userId);
        //支付方式
        if(PayType.WX_PAY.equals(order.getPayType())) {

            if(needClose){
                //TODO 先关闭原来的订单
            }

            WxPaymentInfoVo paymentInfo = wxPayService.requestToPay(
                    openId,
                    order.getOrderNo(),
                    "仲达校园送商品下单",
                    order.getPayPrice().multiply(BigDecimal.valueOf(100)).longValue(),
                    ipAddress, order.getCreateTime(),
                    new DateTime(order.getPaymentDeadline() * 1000).toLocalDateTime()
            );
            //保存支付信息
            OrderPaymentEntity paymentEntity = new OrderPaymentEntity();
            paymentEntity.setOrderTotal(1);
            paymentEntity.setPaymentPackage(JSONObject.toJSONString(paymentInfo));
            paymentEntity.setPayType(order.getPayType());
            if(!orderPaymentService.save(paymentEntity)){
                log.error("重新支付时保存支付信息失败,orderId: {}", order.getId());
                return Result.fail("服务器故障，请稍后重试");
            }
            //重新绑定支付信息
            order.setPaymentId(paymentEntity.getId());
            //返回支付报文
            return Result.success(paymentInfo);
        }
        return Result.fail("未知的支付方式");
    }

    /**
     * 创建订单
     */
    @Override
    @Transactional
    public Result<CreateWxOrderResultVo> createOrders(Long userId, Long loginId, PurchaseConfirmVo purchase, String ipAddress, String payType) {

        if(purchase.getPayPrice() > 99999){
            return Result.fail("交易金额较大，如需继续支付请开放限额");
        }

        //检查是否存在订单异常
        Result<CreateWxOrderResultVo> checkResult = checkOrder(userId, purchase.getStoreList().size());
        if (checkResult != null) return checkResult;

        CreateWxOrderResultVo result = new CreateWxOrderResultVo();

        //查询用户收货地址
        AddressEntity address = addressService.getById(purchase.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) return Result.fail("收货地址不存在");

        //SKU
        Map<String,GoodsSkuEntity> skuMap = new HashMap<>();
        //配送模板
        Map<Long, DeliveryTemplateEntity> deliveryTmpMap = new HashMap<>();
        //配送范围
        Map<Long, DeliveryGroup> deliveryGroupMap = new HashMap<>();
        //skuIds
        List<Long> skuIds = new ArrayList<>();

        //---------------------------------------------------------------------
        //提取数据
        //商品id集合
        Set<Long> goodsIds = new HashSet<>();
        //商店id集合
        List<Long> shopIds = new ArrayList<>();
        //收集配送时间
        Map<Long, LocalDateTime> deliveryTimes = new HashMap<>();
        //查询sku的条件式
        QueryWrapper<GoodsSkuEntity> skuWrapper = new QueryWrapper<>();
        for (PurchaseConfirmStoreVo store : purchase.getStoreList()) {
            shopIds.add(store.getId());
            deliveryTimes.put(store.getId(), store.getDeliveryTime());
            for (PurchaseConfirmGoodsVo goods : store.getGoodsList()) {
                goodsIds.add(goods.getId());
                skuWrapper.or(w->{
                    w.eq("goods_id", goods.getId());
                    w.eq("spec_info", goods.getSku());
                });
            }
        }
        //---------------------------------------------------------------------


        //查询商品
        //---------------------------------------------------------------------
        List<GoodsEntity> goodsList = goodsService.list(
                new QueryWrapper<GoodsEntity>()
                        .in("id", goodsIds)
                        .select("id, name, is_sale")
        );
        Map<Long, GoodsEntity> goodsMap = new HashMap<>();
        for (GoodsEntity goodsEntity : goodsList) {
            goodsMap.put(goodsEntity.getId(), goodsEntity);
        }
        //---------------------------------------------------------------------


        //---------------------------------------------------------------------
        //查询sku
        List<GoodsSkuEntity> skus = goodsSkuService.list(skuWrapper);
        if(skus.size() == 0) return Result.fail("商品不存在");
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
                return Result.fail("商家已暂停营业");
            }
            DeliveryTemplateEntity delivery = deliveryTmpMap.get(shopId);
            LocalDateTime deliveryTime = deliveryTimes.get(shopId);
            LocalDate deliveryDate = deliveryTime.toLocalDate();
            if(deliveryTime.toLocalDate().equals(LocalDate.now())
                    && LocalDateTime.now()
                    .isAfter(
                            deliveryTime.minusMinutes(
                                    //预留时间是给用户备货的
                                    //加上30分钟，是预留出来给用户支付的
                                    delivery.getReserveTime()+30
                            )
                    )
            ){
                return Result.fail("配送时间在商家的预留时间内，请更换配送时间");
            }
            if (!isNotRestDate(deliveryDate, delivery.getRestMonth(), delivery.getRestDay(), delivery.getRestWeek())) {
                return Result.fail("商家当天休息，请更换配送时间");
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
                return Result.fail("当前地址超出配送范围");
            }
        }
        //--------------------------------------------------------------------



        //统一订单创建时间
        LocalDateTime createTime = LocalDateTime.now();

        //按商铺生成订单
        List<OrderEntity> orders = new ArrayList<>();
        int length = purchase.getStoreList().size();
        Map<String, List<OrderGoodsEntity>> orderGoodsListMap = new HashMap<>();
        for (int index = 0; index < length; index++) {

            //生成订单号
            String orderNo = createNo(userId, loginId);
            PurchaseConfirmStoreVo store = purchase.getStoreList().get(index);
            OrderEntity order = new OrderEntity();
            BigDecimal orderPrice = new BigDecimal(0);
            BigDecimal orderDiscount = new BigDecimal(0);
            BigDecimal goodsSumPrice = new BigDecimal(0);
            BigDecimal totalCost = new BigDecimal(0); //订单总成本

            List<OrderGoodsEntity> orderGoodsList = new ArrayList<>();

            for (PurchaseConfirmGoodsVo goods : store.getGoodsList()) {

                //获取商品
                GoodsEntity goodsEntity = goodsMap.get(goods.getId());
                if (goodsEntity == null){
                    return Result.fail("商品已下架");
                }

                //获取sku
                GoodsSkuEntity sku = skuMap.get(goods.getId()+"-"+goods.getSku());
                if(sku == null){
                    return Result.fail("商品不存在");
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
                    return Result.fail("数量不符合购买要求，请重试");
                }

                OrderGoodsEntity orderGoods = new OrderGoodsEntity();
                orderGoods.setCount(goods.getCount());
                orderGoods.setTitle(goodsEntity.getName());
                orderGoods.setSkuInfo(sku.getDescription());
                orderGoods.setThumbnail(sku.getImage());
                orderGoods.setSkuId(sku.getId());
                orderGoods.setUnitPrice(sku.getPrice());
                orderGoods.setPrice(BigDecimal.valueOf(0));
                orderGoods.setDiscount(BigDecimal.valueOf(0));
                orderGoods.setUserId(userId);

                //更新库存
                if (goodsSkuService.changeSafeStockQuantityBySpecInfo(
                        goods.getId(),
                        goods.getSku(),
                        - goods.getCount()   //注意负号不要漏了
                )) {
                    //计算价格
                    BigDecimal price = sku.getPrice().multiply(new BigDecimal(goods.getCount()));
                    goodsSumPrice = goodsSumPrice.add(price);
                    orderPrice = orderPrice.add(price);
                    totalCost = totalCost.add(sku.getCost().multiply(BigDecimal.valueOf(goods.getCount())));

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
                }else{
                    return Result.fail("商品库存不足，请重试");
                }
                orderGoodsList.add(orderGoods);
            }


            orders.add(order);
            order.setOrderNo(orderNo);
            order.setCreateTime(createTime);
            order.setDeliveryTime(deliveryTimes.get(store.getId()));
            order.setDeliveryFee(deliveryGroupMap.get(store.getId()).getDeliveryFee());
            orderPrice = orderPrice.add(order.getDeliveryFee());
            order.setReceiverName(address.getName());
            order.setReceiverMobile(address.getMobile());
            order.setReceiverGender(address.getGender());
            order.setPointId(address.getPointId());
            order.setUserId(userId);
            order.setStatus(PayType.OFFLINE.equals(payType) ? 1 : 0);
            order.setGroupId(address.getGroupId());
            order.setShopId(store.getId());
            order.setPayType(payType);
            order.setRemark(store.getRemark());
            order.setGoodsSumPrice(goodsSumPrice);
            order.setPrice(orderPrice);
            order.setPayPrice(orderPrice.subtract(orderDiscount));  //实付金额在orderPrice计算优惠金额后计算得到
            order.setDiscount(orderDiscount);
            order.setProfit(order.getPayPrice().subtract(totalCost));

            if (!PayType.OFFLINE.equals(order.getPayType())) {
                //非到货订单，需要计算订单支付超时时间
                //订单支付超时时间 = 配送时间 - 准备时长 - 30分钟
                //订单支付超时时长不超过8小时，如果超过就设为8小时
                long deliveryTimestamp = order.getDeliveryTime().toEpochSecond(ZoneOffset.of("+8"));
                long readTime = deliveryTmpMap.get(store.getId()).getReserveTime() * 60;
                long paymentDeadline = deliveryTimestamp - readTime - 1800;
                long lastDeadline = System.currentTimeMillis()/1000 + 28800;
                order.setPaymentDeadline(Math.min(paymentDeadline, lastDeadline));

            }

            //删除购物车中的商品
            cartGoodsService.removeGoods(userId, purchase);


            orderGoodsListMap.put(order.getOrderNo(), orderGoodsList);
        }


        //拉取支付数据
        String openId = userService.getWxOpenid(userId);
        if(orders.size() == 1){
            OrderEntity order = orders.get(0);
            if(order.getPayPrice().doubleValue() != purchase.getPayPrice()){
                return Result.busy();
            }
            //只有一个订单，发起普通支付
            WxPaymentInfoVo payment = wxPayService.requestToPay(
                    openId,
                    order.getOrderNo(),
                    "仲达校园购物商品下单",
                    order.getPayPrice().multiply(BigDecimal.valueOf(100)).longValue(),
                    ipAddress,
                    order.getCreateTime(),
                    new DateTime(order.getPaymentDeadline()*1000).toLocalDateTime()
            );
            //保存支付信息
            OrderPaymentEntity orderPayment = new OrderPaymentEntity();
            orderPayment.setOrderTotal(1);
            orderPayment.setPayType(payType);
            orderPayment.setPaymentPackage(JSONObject.toJSONString(payment));
            if(!orderPaymentService.save(orderPayment)){
                log.error("保存支付信息失败");
                return Result.fail("服务器出现故障，请稍后重试");
            }
            order.setPaymentId(orderPayment.getId());
            result.setWxPaymentInfo(payment);
        }else{
            //多订单模式
            result.setIsMultiOrder(true);
            double totalPayPrice = 0;
            for (OrderEntity order : orders) {
                totalPayPrice += order.getPayPrice().doubleValue();
            }
            if(totalPayPrice != purchase.getPayPrice()){
                return Result.busy();
            }
        }

        //创建订单
        if(saveBatch(orders)){
            List<Long> orderIds = new ArrayList<>();
            List<OrderGoodsEntity> orderGoodsEntities = new ArrayList<>();
            for (OrderEntity order : orders) {
                //设置订单自动关闭任务
                if(!PayType.OFFLINE.equals(order.getPayType())){
                    if(!setOrderAutoCloseTask(order.getId(), order.getPaymentDeadline()*1000)){
                        log.error("订单自动关闭任务设置失败，请维护人员立即检查");
                        return Result.fail("服务器出现错误，请稍后重试");
                    }
                }
                orderIds.add(order.getId());
                //创建订单的商品数据
                List<OrderGoodsEntity> orderGoods = orderGoodsListMap.get(order.getOrderNo());
                for (OrderGoodsEntity orderGood : orderGoods) {
                    orderGood.setOrderId(order.getId());
                    orderGoodsEntities.add(orderGood);
                }
            }
            result.setOrderIds(orderIds);
            orderGoodsService.saveBatch(orderGoodsEntities);
        }


        return Result.success(result);
    }

    /**
     * 生成编号
     */
    private String createNo(Long userId, Long loginId){
        Long localDate = System.currentTimeMillis();
        return String.format("%10d%04d%04d",localDate, userId, loginId);
    }

    /**
     * 判断当天是否是商家的休息时间
     */
    private boolean isNotRestDate(LocalDate date, Integer restMonth, Long restDay, Integer restWeek){
        return ((restMonth >> (date.getMonthValue() - 2)) & 1) == 0   //当月休息
                && ((restDay >> (date.getDayOfMonth() - 1)) & 1) == 0 //该日期休息
                && ((restWeek >> (date.getDayOfWeek().getValue()) - 1) & 1) == 0;  //该周天休息
    }


    @Override
    public List<OrderStoreVo> details(Long userId, List<Long> ids){

        List<OrderEntity> orders = list(
                new QueryWrapper<OrderEntity>()
                        .in("id", ids)
                        .eq("user_id", userId)
        );
        if(orders.size() == 0) return null;

        Set<Long> pointIds = new HashSet<>();
        Set<Long> groupIds = new HashSet<>();
        Set<Long> shopIds = new HashSet<>();
        for (OrderEntity order : orders) {
            pointIds.add(order.getPointId());
            shopIds.add(order.getShopId());
            groupIds.add(order.getGroupId());
        }

        //查询商铺名称
        Map<Long, String> shopNames = shopService.shopNamesMap(shopIds);

        //获取端点名称
        Map<Long, String> pointNames = pointService.getPointNames(pointIds);

        //获取楼栋名称
        Map<Long, String> groupNames = groupService.getGroupNames(groupIds);

        //获取订单所有配送员
        List<OrderDeliveryEntity> orderDeliveryEntities = orderDeliveryService.list(
                new QueryWrapper<OrderDeliveryEntity>()
                        .in("order_id", ids)
                        .select("carrier_id, order_id")
        );
        Map<Long, Long> orderCarrierIdMap = new HashMap<>();
        for (OrderDeliveryEntity d : orderDeliveryEntities) {
            orderCarrierIdMap.put(d.getOrderId(), d.getCarrierId());
        }
        Map<Long, CarrierEntity> carrierMap = new HashMap<>();
        if(orderCarrierIdMap.size() > 0) {
            //获取所有配送员信息
            List<CarrierEntity> carriers = carrierService.list(
                    new QueryWrapper<CarrierEntity>()
                            .in("id", orderCarrierIdMap.values())
                            .select("id, name, mobile")
            );
            for (CarrierEntity carrier : carriers) {
                carrierMap.put(carrier.getId(), carrier);
            }
        }



        List<OrderGoodsEntity> goodsList = orderGoodsService.list(
                new QueryWrapper<OrderGoodsEntity>()
                        .in("order_id", ids)
                        .eq("user_id", userId)
        );

        List<OrderStoreVo> res = new ArrayList<>();
        Map<Long, OrderStoreVo> orderStoreVoMap = new HashMap<>();
        for (OrderEntity order : orders) {
            OrderStoreVo o = new OrderStoreVo();
            o.setOrderId(order.getId());
            o.setShopId(order.getShopId());
            o.setShopName(shopNames.getOrDefault(o.getShopId(), "店铺"+order.getShopId()));
            o.setRemark(order.getRemark());
            o.setStatus(order.getStatus());
            o.setFinishTime(order.getFinishTime());
            o.setPaymentDeadline(order.getPaymentDeadline());
            o.setDeliveryTime(order.getDeliveryTime());
            o.setCreateTime(order.getCreateTime());
            o.setOrderNo(order.getOrderNo());
            o.setAddress(
                    groupNames.getOrDefault(order.getGroupId(),"**")
                            +" "
                            + pointNames.getOrDefault(order.getPointId(),"**")
            );
            o.setReceiverName(order.getReceiverName());
            o.setReceiverMobile(order.getReceiverMobile());
            o.setPayType(order.getPayType());
            o.setPayTime(order.getPayTime());
            o.setCreateTime(order.getCreateTime());
            o.setDiscount(order.getDiscount());
            o.setTradeNo(order.getTradeNo());
            o.setPrice(order.getPrice());
            o.setPayPrice(order.getPayPrice());
            o.setGoodsSumPrice(order.getGoodsSumPrice());
            o.setDeliveryFee(order.getDeliveryFee());
            o.setGoodsList(new ArrayList<>());
            res.add(o);

            //配送员信息
            if(orderCarrierIdMap.containsKey(order.getId())) {
                Long carrierId = orderCarrierIdMap.get(order.getId());
                if(carrierMap.containsKey(carrierId)){
                    CarrierEntity carrier = carrierMap.get(carrierId);
                    if (carrier != null) {
                        o.setCarrierContact(carrier.getMobile());
                        o.setCarrierId(carrier.getId());
                        o.setCarrierName(carrier.getName());
                    }
                }
            }


            orderStoreVoMap.put(o.getOrderId(), o);
        }



        for (OrderGoodsEntity goods : goodsList) {
            OrderGoodsVo g = new OrderGoodsVo();
            g.setSkuInfo(goods.getSkuInfo());
            g.setTitle(goods.getTitle());
            g.setThumb(goods.getThumbnail());
            g.setCount(goods.getCount());
            g.setPrice(goods.getPrice());
            if(orderStoreVoMap.containsKey(goods.getOrderId())){
                orderStoreVoMap.get(goods.getOrderId()).getGoodsList().add(g);
            }
        }

        return res;
    }

    /**
     * 为订单分配配送员
     */
    @Transactional
    private void assignCarrier(Long shopId, OrderEntity order){
        //查出今日排班的配送员
        int day = LocalDate.now().getDayOfMonth();
        CarrierEntity carrier = carrierService.getOne(new QueryWrapper<CarrierEntity>()
                .eq("shop_id", shopId)
                .eq("(active_day >> " + (day-1) + ") & 1", 1)
                .orderByDesc("weight")
                .last("limit 1")
        );
        //更新配送员权重
        if (!carrierService.update(new UpdateWrapper<CarrierEntity>()
                .eq("id", carrier != null ? carrier.getId() : null)
                .setSql("weight = weight - 1")
        )) {
            log.error("配送员权重更新失败");
            return;
        }
        //分配订单
        OrderDeliveryEntity orderDelivery = new OrderDeliveryEntity();
        orderDelivery.setCarrierId(carrier != null ? carrier.getId() : null);
        orderDelivery.setDeliveryTime(order.getDeliveryTime());
        orderDelivery.setOrderId(order.getId());
        orderDelivery.setStatus(OrderDeliveryStatus.ASSIGN);
        orderDelivery.setCommission(order.getProfit().multiply(BigDecimal.valueOf(0.1)));  //佣金
        if (!orderDeliveryService.save(orderDelivery)) {
            log.error("订单分配失败");
        }
    }

    @Override
    @Transactional
    public Result<?> onWxPayed(Long userId, String orderNo, String tradeNo, Integer totalFee, LocalDateTime payTime) {

        //查询订单
        OrderEntity order = getOne(new QueryWrapper<OrderEntity>()
                .eq("user_id", userId)
                .eq("order_no", orderNo)
                .eq("pay_type", PayType.WX_PAY)
                .last("limit 1")
        );
        if(order == null){
            return Result.fail("订单不存在");
        }
        if(order.getPayPrice().multiply(BigDecimal.valueOf(100)).intValue() != totalFee){
            return Result.fail("订单金额不一致");
        }

        order.setTradeNo(tradeNo);
        order.setPayTime(payTime);
        order.setStatus(1);

        //更新订单状态
        if(!update(order, new QueryWrapper<OrderEntity>()
                .eq("id", order.getId())
                .eq("status", 0)    //订单待支付时才能支付
        )){
            return Result.fail("订单状态更新失败");
        }
        //移除订单自动关闭任务
        if(!removeCloseOrderTask(Set.of(order.getId()+""))){
            log.warn("移除自动关闭订单任务失败 --> 订单ID:{}", order.getId());
        }

        //分配配送员
        assignCarrier(order.getShopId(), order);

        return Result.success();
    }

    /**
     * 微信退款成功
     */
    @Override
    public JSONObject onWxRefundSuccess(String outRefundNo, String refundNo, LocalDateTime refundTime, BigDecimal total, BigDecimal refund) {
        OrderRefundEntity refundEntity = refundService.getOne(
                new QueryWrapper<OrderRefundEntity>()
                        .eq("out_refund_no", outRefundNo)
                        .last("limit 1")
        );
        if(refund == null) {
            JSONObject res = new JSONObject();
            res.put("code", "SUCCESS");
            res.put("message", "退款订单不存在");
            return res;
        }
        //查找订单列表
        List<Long> orderIds = new ArrayList<>();
        List<OrderRefundMapEntity> binds = refundMapService.list(new QueryWrapper<OrderRefundMapEntity>()
                .eq("refund_id", refundEntity.getId())
        );
        for (OrderRefundMapEntity bind : binds) {
            orderIds.add(bind.getOrderId());
        }
        //更新订单状态
        if (!update(new UpdateWrapper<OrderEntity>()
                .in("id", orderIds)
                .set("status", OrderStatus.REFUNDED)
        )) {
            JSONObject res = new JSONObject();
            res.put("code", "SUCCESS");
            res.put("message", "订单状态更新失败");
            return res;
        }
        //更新退款单状态
        if (!refundService.update(
                new UpdateWrapper<OrderRefundEntity>()
                        .eq("id", refundEntity.getId())
                        .set("status", RefundStatus.SUCCESS)
        )) {
            JSONObject res = new JSONObject();
            res.put("code", "SUCCESS");
            res.put("message", "退款订单状态更新失败");
            return res;
        }
        //更新配送单状态
        if(!orderDeliveryService.update(
                new UpdateWrapper<OrderDeliveryEntity>()
                        .in("order_id", orderIds)
                        .set("status", OrderDeliveryStatus.CANCEL)
        )){
            JSONObject res = new JSONObject();
            res.put("code", "SUCCESS");
            res.put("message", "配送单状态更新失败");
            return res;
        }
        //库存回滚
        List<OrderGoodsEntity> goodsList = orderGoodsService.list(new QueryWrapper<OrderGoodsEntity>()
                .in("order_id", orderIds)
        );
        for (OrderGoodsEntity goods : goodsList) {
            goodsSkuService.changeSafeStockQuantity(goods.getSkuId(), goods.getCount());
        }

        //TODO 删除评论
        return null;
    }

    /**
     * 微信退款失败
     */
    @Override
    public JSONObject onWxRefundFail(String outRefundNo, String message, LocalDateTime time) {
        log.error("退款失败"+outRefundNo+","+message+","+time);
        return null;
    }

    /**
     * 用户退款
     */
    @Transactional
    @Override
    public Result<?> requireRefund(Long userId, Long loginId, Long orderId, Integer reason, String message){
        //获取订单
        OrderEntity order = getOne(new QueryWrapper<OrderEntity>()
                .eq("user_id", userId)
                .eq("id", orderId)
                .in("status",
                        //支持退款的订单
                        List.of(
                                OrderStatus.READY,  //已支付的订单
                                OrderStatus.DELIVERING,  //配送中的订单
                                OrderStatus.ARRIVED,    //已送达的订单
                                OrderStatus.REVIEWED,    //已评论的订单
                                OrderStatus.REFUNDING   //已提交退款申请的
                        )
                )
                .last("limit 1")
        );
        if(order == null)
            return Result.fail("该订单当前无法退款");
        if(order.getStatus().equals(OrderStatus.REFUNDING)){
            return Result.fail("退款申请已经提交，请勿重复提交");
        }


        OrderRefundEntity refund = null;
        //查找是否存在绑定
        boolean haveToBind = false;
        OrderRefundMapEntity bind = refundMapService.getOne(new QueryWrapper<OrderRefundMapEntity>()
                .eq("order_id", orderId)
        );
        if(bind != null){
            //存在绑定，直接使用之前的
            refund = refundService.getById(bind.getRefundId());
        }else {
            //需要创建绑定
            haveToBind = true;
        }
        if(refund == null){
            //不存在，创建退款订单
            String outRefundNo = createNo(userId, loginId);
            refund = new OrderRefundEntity();
            refund.setOutRefundNo(outRefundNo);
        }

        refund.setTradeNo(order.getTradeNo());
        refund.setUserId(userId);
        refund.setShopId(order.getShopId());
        refund.setOriginOrderStatus(order.getStatus());
        refund.setRefund(order.getPayPrice());
        refund.setOriginTotal(order.getPayPrice());
        refund.setPayType(order.getPayType());
        refund.setCreateTime(LocalDateTime.now());
        refund.setReason(reason);

        //如果未开始发货，直接自动退款
        refund.setStatus(
                Objects.equals(OrderStatus.READY, order.getStatus())
                        ?
                        RefundStatus.PROCESSING
                        : RefundStatus.SUBMITTED
        );
        refund.setMessage(message);
        if(!refundService.saveOrUpdate(refund) || refund.getId() == null) {
            log.error("退款订单创建失败");
            return Result.fail("退款订单创建失败");
        }

        if(haveToBind){
            //创建订单退款单绑定
            refundMapService.save(new OrderRefundMapEntity(refund.getId(), orderId));
        }
        //修改订单状态
        if (!update(new UpdateWrapper<OrderEntity>()
                .eq("id", orderId)
                .eq("status",order.getStatus())
                .set("status", OrderStatus.REFUNDING)
        )) {
            return Result.busy();
        }

        //自动退款
        if(refund.getStatus().equals(RefundStatus.PROCESSING)) {
            List<OrderGoodsEntity> orderGoodsList = orderGoodsService.list(
                    new QueryWrapper<OrderGoodsEntity>()
                            .eq("order_id", orderId)
            );
            List<WxOrderGoodsEntity> goodsList = new ArrayList<>();
            for (OrderGoodsEntity g : orderGoodsList) {
                WxOrderGoodsEntity wg = new WxOrderGoodsEntity();
                wg.setGoodsName(g.getTitle());
                wg.setRefundAmount(g.getPrice().multiply(BigDecimal.valueOf(100)).longValue());
                wg.setUnitPrice(g.getUnitPrice().multiply(BigDecimal.valueOf(100)).longValue());
                wg.setRefundQuantity(g.getCount());
                wg.setMerchantGoodsId(g.getSkuId() + "");
                goodsList.add(wg);
            }
            wxRefundService.requestToRefund(
                    refund.getTradeNo(),
                    refund.getOutRefundNo(),
                    refund.getRefund().multiply(BigDecimal.valueOf(100)).longValue(),
                    refund.getOriginTotal().multiply(BigDecimal.valueOf(100)).longValue(),
                    goodsList,
                    RefundReasons.getReasonValue(refund.getReason())
            );
        }


        return Result.success();
    }

    @Override
    public Result<?> checkReceiveCode(Long orderId, String code){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String codeEncrypt = ops.get(RECEIVE_CODE_KEY + ":" + orderId);
        if(codeEncrypt == null){
            return Result.fail("取货码已过期");
        }
        try {
            if(!MD5Utils.md5Hex(
                    ("orderId="+orderId+"&code="+code).getBytes()
            ).toUpperCase().equals(codeEncrypt)){
                return Result.fail("取货码错误");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return Result.success();
    }


    /**
     * 设置取货码
     * 配送员送货上门时，需要买家出示取货码，验证成功方可交货
     * 取货码经用户本地生成，在加盐MD5加密后上传至服务器
     * （加密格式order_id=订单编号&receive_code=取货码）
     * 在任何情况下，如果买家不出示取货码，任何人将无法知道
     * @param userId 用户id
     * @param orderId 订单id
     * @param code 取货码
     */
    @Override
    public Result<String> setReceiveCode(Long userId, Long orderId, String code) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        OrderEntity order = getOne(
                new LambdaQueryWrapper<OrderEntity>()
                        .eq(OrderEntity::getId, orderId)
                        .eq(OrderEntity::getUserId, userId)
                        .select(OrderEntity::getOrderNo,OrderEntity::getStatus)

        );
        if(order == null){
            return Result.fail("订单不存在");
        }
        switch (order.getStatus()){
            case OrderStatus.READY:
            case OrderStatus.DELIVERING: break;
            case OrderStatus.ARRIVED:
            case OrderStatus.REVIEWED: return Result.fail("订单已送达");
            default: return Result.fail("该订单无法取货");
        }
        ops.set(RECEIVE_CODE_KEY+":"+orderId, code,310, TimeUnit.SECONDS);
        return Result.success(null,order.getOrderNo());
    }

    /**
     * 订阅送达消息
     */
    @Override
    public boolean subscribeReceiveMessage(Long userId, List<Long> orderIds){
        return update(
                new UpdateWrapper<OrderEntity>()
                        .eq("user_id", userId)
                        .in("id", orderIds)
                        .set("receive_arrive_message",1)
        );
    }
}
