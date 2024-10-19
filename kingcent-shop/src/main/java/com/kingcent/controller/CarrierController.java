package com.kingcent.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.common.entity.result.Result;
import com.kingcent.entity.vo.carrier.CarrierDeliveryVo;
import com.kingcent.entity.vo.carrier.DeliveryOrderVo;
import com.kingcent.service.*;
import com.kingcent.common.shop.constant.OrderDeliveryStatus;
import com.kingcent.common.shop.entity.CarrierEntity;
import com.kingcent.common.shop.entity.OrderDeliveryEntity;
import com.kingcent.common.shop.entity.OrderEntity;
import com.kingcent.common.shop.entity.OrderGoodsEntity;
import com.kingcent.common.user.utils.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/25 14:19
 */
@RestController
@RequestMapping("/carrier")
public class CarrierController {

    @Autowired
    private CarrierService carrierService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private GroupPointService pointService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private OrderDeliveryService orderDeliveryService;

    @PostMapping("/finish")
    public Result<?> finish(HttpServletRequest request, Long orderId, String code){
        return orderDeliveryService.finish(
                RequestUtil.getUserId(request),
                orderId,
                code
        );
    }

    @GetMapping("/delivery_list")
    public Result<CarrierDeliveryVo> deliveryList(HttpServletRequest request, String date, String statuses){
        Long userId = RequestUtil.getUserId(request);
        LocalDate today = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        CarrierEntity carrier = carrierService.getOne(
                new QueryWrapper<CarrierEntity>()
                        .eq("user_id", userId)
        );
        if(carrier == null){
            return Result.fail("非配送员");
        }

        Map<String, Object> day0 = orderDeliveryService.getMap(
                new QueryWrapper<OrderDeliveryEntity>()
                        .eq("carrier_id", carrier.getId())
                        .ge("delivery_time", today)
                        .lt("delivery_time", today.plusDays(1))
                        .in("status", List.of(
                                OrderDeliveryStatus.ASSIGN,
                                OrderDeliveryStatus.CONFIRM
                                , OrderDeliveryStatus.FINISH)
                        )
                        .select("COUNT(order_id) AS day_total_order")
        );
        Map<String, Object> day1 = orderDeliveryService.getMap(
                new QueryWrapper<OrderDeliveryEntity>()
                        .eq("carrier_id", carrier.getId())
                        .ge("delivery_time", today)
                        .lt("delivery_time", today.plusDays(1))
                        .eq("status", OrderDeliveryStatus.FINISH)
                        .select("COUNT(order_id) AS day_finish_order, SUM(commission) AS day_commission")
        );
        LocalDate thisMonth = LocalDate.of(today.getYear(), today.getMonth(), 1);
        Map<String, Object> month = orderDeliveryService.getMap(
                new QueryWrapper<OrderDeliveryEntity>()
                        .eq("carrier_id", carrier.getId())
                        .ge("delivery_time", thisMonth)
                        .lt("delivery_time",thisMonth.plusMonths(1))
                        .eq("status", OrderDeliveryStatus.FINISH)
                        .select("COUNT(order_id) AS month_finish_order, SUM(commission) AS month_commission")
        );

        CarrierDeliveryVo res = new CarrierDeliveryVo();
        res.setDayCommission(day1 != null ? (BigDecimal) day1.get("day_commission") : BigDecimal.valueOf(0));
        res.setDayOrderCount(day0 != null ? (Long) day0.get("day_total_order") : 0);
        res.setDeliveredOrderCount(day1 != null ? (Long) day1.get("day_finish_order") : 0);
        res.setMonthCommission(month != null ? (BigDecimal) month.get("month_commission") : BigDecimal.valueOf(0));
        res.setMouthDeliveredOrderCount(month != null ? (Long) month.get("month_finish_order") : 0);

        Map<Long, DeliveryOrderVo> orderMap = new HashMap<>();
        List<DeliveryOrderVo> orderList = new ArrayList<>();
        List<OrderDeliveryEntity> deliveryOrders = orderDeliveryService.list(
                new QueryWrapper<OrderDeliveryEntity>()
                        .eq("carrier_id", carrier.getId())
                        .in("status", Arrays.stream(statuses.split(",")).toList())
                        .ge("delivery_time", today)
                        .lt("delivery_time", today.plusDays(1))
        );

        if(deliveryOrders.size() == 0){
            return Result.success(res);
        }


        List<Long> orderIds = new ArrayList<>();
        for (OrderDeliveryEntity deliveryOrder : deliveryOrders) {
            orderIds.add(deliveryOrder.getOrderId());
            DeliveryOrderVo order = new DeliveryOrderVo();
            order.setOrderId(deliveryOrder.getOrderId());
            order.setId(deliveryOrder.getId());
            order.setStatus(deliveryOrder.getStatus());
            order.setCommission(deliveryOrder.getCommission());
            orderMap.put(deliveryOrder.getOrderId(),order);
            orderList.add(order);
        }

        Set<Long> pointIds = new HashSet<>();
        Set<Long> groupIds = new HashSet<>();

        List<OrderEntity> orders = orderService.listByIds(orderIds);
        for (OrderEntity o : orders) {
            DeliveryOrderVo order = orderMap.get(o.getId());
            order.setName(o.getReceiverName());
            order.setRemark(o.getRemark());
            order.setPhone(o.getReceiverMobile());
            pointIds.add(o.getPointId());
            groupIds.add(o.getGroupId());
        }

        Map<Long, String> pointNames = pointService.getPointNames(pointIds);
        Map<Long, String> groupNames = groupService.getGroupNames(groupIds);

        for (OrderEntity order : orders) {
            DeliveryOrderVo o = orderMap.get(order.getId());
            o.setAddress(
                    groupNames.getOrDefault(order.getGroupId(),"")
                            +pointNames.getOrDefault(order.getPointId(),"")
            );
        }


        Map<Long, List<String>> orderGoodsMap = new HashMap<>();
        List<OrderGoodsEntity> goodsList = orderGoodsService.list(
                new QueryWrapper<OrderGoodsEntity>()
                        .in("order_id", orderIds)
        );
        for (OrderGoodsEntity g : goodsList) {
            List<String> goodsInfo = orderGoodsMap.computeIfAbsent(g.getOrderId(), w -> new ArrayList<>());
            goodsInfo.add(g.getTitle()+" "+g.getSkuInfo()+" ×"+g.getCount());
            DeliveryOrderVo order = orderMap.get(g.getOrderId());
            order.setGoods(goodsInfo);
        }
        res.setOrders(orderList);
        return Result.success(res);
    }

}
