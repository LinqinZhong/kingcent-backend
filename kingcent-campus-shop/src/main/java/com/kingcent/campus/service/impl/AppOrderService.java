package com.kingcent.campus.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.service.*;
import com.kingcent.campus.shop.constant.OrderStatus;
import com.kingcent.campus.shop.constant.PayType;
import com.kingcent.campus.shop.constant.RefundStatus;
import com.kingcent.campus.shop.entity.*;
import com.kingcent.campus.wx.entity.vo.CreateWxOrderResultVo;
import com.kingcent.campus.shop.entity.vo.order.OrderGoodsVo;
import com.kingcent.campus.shop.entity.vo.order.OrderStoreVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmGoodsVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmStoreVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.listener.OrderListener;
import com.kingcent.campus.shop.mapper.OrderMapper;
import com.kingcent.campus.wx.entity.vo.WxPaymentInfoVo;
import com.kingcent.campus.wx.service.WxOrderService;
import com.kingcent.campus.wx.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

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
    private PayTypeService payTypeService;

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

    private static final String MESSAGE_KEY = "message:queue:order:dead";




    //订单超时自动关闭暂时使用redis作为消息队列，后期可能会使用其它mq中间件
    //---------------------------------------------------------------------------
    //TODO 改成定时任务
    /**
     * 监听超时订单
     */
    @Override
    public void listenOrderDead(OrderListener orderListener) {
        ZSetOperations<String, String> ops = redisTemplate.opsForZSet();

        while (true){
            long current = System.currentTimeMillis() + 1000;
            //一次处理10条
            Set<String> set = ops.rangeByScore(
                            MESSAGE_KEY,
                            0,
                            current,
                            0,
                            10
                    );
            if(set != null) {
                if (set.isEmpty()) {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    orderListener.onOverTime(set);
                    //删除
                    for (String s : set) {
                        ops.remove(MESSAGE_KEY, s);
                    }
                }
            }else {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

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
    public boolean closeOrder(List<Long> orderIds){
        //过滤出可以关闭的订单
        List<OrderEntity> orderUnclose = list(
                new QueryWrapper<OrderEntity>()
                        .or(w->w.eq("status", OrderStatus.NOT_PAY))
                        .or(w->{
                            w.eq("pay_type", PayType.OFFLINE);
                            w.eq("status", OrderStatus.READY);
                        })
                        .in("id", orderIds)
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
                if(!goodsSkuService.update(
                        new UpdateWrapper<GoodsSkuEntity>()
                                .eq("id", goods.getSkuId())
                                .setSql("safe_stock_quantity = safe_stock_quantity + " + goods.getCount())
                )) {
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
     * 创建订单
     */
    @Override
    @Transactional
    public Result<CreateWxOrderResultVo> createOrders(Long userId, Long loginId, PurchaseConfirmVo purchase, String ipAddress) {

        //检查是否存在订单异常
        Result<CreateWxOrderResultVo> checkResult = checkOrder(userId, purchase.getStoreList().size());
        if (checkResult != null) return checkResult;

        CreateWxOrderResultVo result = new CreateWxOrderResultVo();

        //查询用户收货地址
        AddressEntity address = addressService.getById(purchase.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) return Result.fail("收货地址不存在");

        //所有微信支付的金额
        BigDecimal wxPayPrice = new BigDecimal(0);

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
        //商品id集合
        Set<Long> goodsIds = new HashSet<>();
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

        //--------------------------------------------------------------------
        //查询店铺支持的支付方式
        List<PayTypeEntity> payTypes = payTypeService.list(payTypeWrapper);
        for (PayTypeEntity payType : payTypes) {
            payTypeMaps.put(payType.getShopId(), payType.getType());
        }
        for (Long shopId : shopIds) {
            if (!payTypeMaps.containsKey(shopId)){
                return Result.fail("不支持该支付方式，请稍后重试");
            }
        }
        //--------------------------------------------------------------------


        //生成统一的outTradeNo
        String outTradeNo = createNo(userId, loginId);
        //统一订单创建时间
        LocalDateTime createTime = LocalDateTime.now();

        //按商铺生成订单
        List<OrderEntity> orders = new ArrayList<>();
        int length = purchase.getStoreList().size();
        Map<String, List<OrderGoodsEntity>> orderGoodsListMap = new HashMap<>();
        for (int index = 0; index < length; index++) {
            PurchaseConfirmStoreVo store = purchase.getStoreList().get(index);
            OrderEntity order = new OrderEntity();
            BigDecimal orderPrice = new BigDecimal(0);
            BigDecimal orderDiscount = new BigDecimal(0);
            BigDecimal goodsSumPrice = new BigDecimal(0);

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
                    goodsSumPrice = goodsSumPrice.add(price);
                    orderPrice = orderPrice.add(price);

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

            String payType = payTypeMaps.get(store.getId());


            orders.add(order);
            order.setOrderNo(outTradeNo+""+String.format("%02d",index));
            order.setOutTradeNo(outTradeNo);
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

            if (!PayType.OFFLINE.equals(order.getPayType())) {
                //非到货订单，需要计算价格和订单支付超时时间

                //微信支付订单
                if(PayType.WX_PAY.equals(order.getPayType()))
                    wxPayPrice = wxPayPrice.add(orderPrice);

                //未知的支付方式，退出（应该不会出现，上面做了判断）
                else return Result.fail("不支持该支付方式,"+order.getPayType());

                //订单支付超时时间 = 配送时间 - 准备时长 - 30分钟
                //订单支付超时时长不超过8小时，如果超过就设为8小时
                long deliveryTimestamp = order.getDeliveryTime().toEpochSecond(ZoneOffset.of("+8"));
                long readTime = deliveryTmpMap.get(store.getId()).getReserveTime() * 60;
                long paymentDeadline = deliveryTimestamp - readTime - 1800;
                long lastDeadline = System.currentTimeMillis()/1000 + 28800;
                order.setPaymentDeadline(Math.min(paymentDeadline, lastDeadline));

            }

            orderGoodsListMap.put(order.getOrderNo(), orderGoodsList);
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


        //需要线上支付
        if(wxPayPrice.doubleValue() > 0){
            //获取用户信息
            String wxOpenid = userService.getWxOpenid(userId);
            if(wxOpenid.equals("")){
                return Result.fail("微信用户不存在");
            }
            //获取微信支付请求信息
            WxPaymentInfoVo wxPaymentInfo = wxPayService.requestToPay(
                    wxOpenid,
                    outTradeNo,
                    "仲达校园送商品下单",
                    wxPayPrice.multiply(new BigDecimal(100)).longValue(),
                    ipAddress,
                    createTime
            );
            result.setWxPaymentInfo(wxPaymentInfo);
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

        //获取配送点名称
        Map<Long, String> groupNames = groupService.getGroupNames(groupIds);


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

    @Override
    @Transactional
    public Result<?> onWxPayed(Long userId, String outTradeNo, String tradeNo, Integer totalFee, LocalDateTime payTime) {
        List<OrderEntity> list = list(new QueryWrapper<OrderEntity>()
                .eq("user_id", userId)
                .eq("out_trade_no", outTradeNo)
                .eq("pay_type", PayType.WX_PAY)
        );
        Set<String> orderIds = new HashSet<>();
        BigDecimal totalPrice = new BigDecimal(0);
        for (OrderEntity order : list) {
            orderIds.add(order.getId()+"");
            order.setTradeNo(tradeNo);
            order.setPayTime(payTime);
            order.setStatus(1);
            totalPrice = totalPrice.add(order.getPrice());
        }
        if(totalPrice.multiply(BigDecimal.valueOf(100)).longValue() != totalFee){
            return Result.fail("订单金额不一致");
        }
        //更新订单状态
        BigDecimal refundPrice = new BigDecimal(0); //未处理成功的订单计入退款
        for (OrderEntity order : list) {
            //逐条更新，取出异常订单
            if(!update(order, new QueryWrapper<OrderEntity>()
                    .eq("id", order.getId())
                    .eq("status", 0)    //订单待支付时才能支付
            )){
                refundPrice = refundPrice.add(order.getPayPrice());
            }
        }
        //移除订单自动关闭任务
        if(!removeCloseOrderTask(orderIds)){
            log.warn("移除自动关闭订单任务失败 --> 订单ID:{}", orderIds);
        }

        //TODO 退款
        log.info("订单处理成功 -> 订单ID:{}，总金额:{}，退款金额:{}", orderIds,totalPrice,refundPrice);

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
                                OrderStatus.RECEIVED,    //已收货的订单
                                OrderStatus.REVIEWED    //已评论的订单
                        )
                )
                .last("limit 1")
        );
        if(order == null)
            return Result.fail("该订单当前无法退款");

        //获取同批次支付的订单总金额
        BigDecimal total = (BigDecimal) getMap(
                new QueryWrapper<OrderEntity>()
                        .eq("trade_no", order.getTradeNo())
                        .select("SUM(pay_price) AS total")
        ).get("total");

        //创建退款订单
        String outRefundNo = createNo(userId, loginId);
        OrderRefundEntity refund = new OrderRefundEntity();
        refund.setOutRefundNo(outRefundNo);
        refund.setTradeNo(order.getTradeNo());
        refund.setRefund(total);
        refund.setOriginTotal(total);
        refund.setPayType(order.getPayType());
        refund.setCreateTime(LocalDateTime.now());
        refund.setReason(reason);
        refund.setStatus(RefundStatus.SUBMITTED);
        refund.setMessage(message);
        if(!refundService.save(refund) || refund.getId() == null) {
            log.error("退款订单创建失败");
            return Result.fail("退款订单创建失败");
        }

        //创建订单退款单映射
        refundMapService.save(new OrderRefundMapEntity(refund.getId(), orderId));

        //修改订单状态
        if (!update(new UpdateWrapper<OrderEntity>()
                .eq("id", orderId)
                .eq("status",order.getStatus())
                .set("status", OrderStatus.REFUNDING)
        )) {
            return Result.busy();
        }
        return Result.success();
    }
}
