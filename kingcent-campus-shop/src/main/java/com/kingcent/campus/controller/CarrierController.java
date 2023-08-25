package com.kingcent.campus.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.carrier.DeliveryOrder;
import com.kingcent.campus.service.*;
import com.kingcent.campus.shop.constant.OrderDeliveryStatus;
import com.kingcent.campus.shop.entity.CarrierEntity;
import com.kingcent.campus.shop.entity.OrderDeliveryEntity;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.OrderGoodsEntity;
import com.kingcent.campus.shop.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<List<DeliveryOrder>> deliveryList(HttpServletRequest request){
        Long userId = RequestUtil.getUserId(request);
        CarrierEntity carrier = carrierService.getOne(
                new QueryWrapper<CarrierEntity>()
                        .eq("user_id", userId)
        );
        if(carrier == null){
            return Result.fail("非配送员");
        }
        Map<Long,DeliveryOrder> orderMap = new HashMap<>();
        List<DeliveryOrder> result = new ArrayList<>();
        List<OrderDeliveryEntity> deliveryOrders = orderDeliveryService.list(
                new QueryWrapper<OrderDeliveryEntity>()
                        .eq("carrier_id", carrier.getId())
                        .in("status",List.of(
                                OrderDeliveryStatus.ASSIGN,
                                OrderDeliveryStatus.CONFIRM
                        ))
        );

        List<Long> orderIds = new ArrayList<>();
        for (OrderDeliveryEntity deliveryOrder : deliveryOrders) {
            orderIds.add(deliveryOrder.getOrderId());
            DeliveryOrder order = new DeliveryOrder();
            order.setOrderId(deliveryOrder.getOrderId());
            order.setId(deliveryOrder.getId());
            order.setStatus(deliveryOrder.getStatus());
            order.setCommission(deliveryOrder.getCommission());
            orderMap.put(deliveryOrder.getOrderId(),order);
            result.add(order);
        }

        Set<Long> pointIds = new HashSet<>();
        Set<Long> groupIds = new HashSet<>();
        List<OrderEntity> orders = orderService.listByIds(orderIds);
        for (OrderEntity o : orders) {
            DeliveryOrder order = orderMap.get(o.getId());
            order.setName(o.getReceiverName());
            order.setRemark(o.getRemark());
            order.setPhone(o.getReceiverMobile());
            pointIds.add(o.getPointId());
            groupIds.add(o.getGroupId());
        }

        Map<Long, String> pointNames = pointService.getPointNames(pointIds);
        Map<Long, String> groupNames = groupService.getGroupNames(groupIds);

        for (OrderEntity order : orders) {
            DeliveryOrder o = orderMap.get(order.getId());
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
            DeliveryOrder order = orderMap.get(g.getOrderId());
            order.setGoods(goodsInfo);
        }
        return Result.success(result);
    }

}
