package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.constant.OrderStatus;
import com.kingcent.campus.shop.constant.PayType;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.vo.order.OrderStoreVo;
import com.kingcent.campus.shop.entity.vo.order.OrderVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.shop.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/confirm")
    @ResponseBody
    public Result<?> confirmOrder(HttpServletRequest request, @RequestBody PurchaseConfirmVo confirmVo){
        return orderService.createOrders(RequestUtil.getUserId(request), RequestUtil.getLoginId(request), confirmVo);
    }

    @GetMapping("/list/{page}")
    @ResponseBody
    public Result<VoList<OrderStoreVo>> list(HttpServletRequest request, @PathVariable Integer page, @RequestParam(required = false) Integer status){
        VoList<OrderStoreVo> res = orderService.orderList(RequestUtil.getUserId(request), status, page);
        return Result.success(res);
    }

    @GetMapping("/close/{orderId}")
    @ResponseBody
    public Result<?> closeOrder(HttpServletRequest request, @PathVariable Long orderId){
        OrderEntity order = orderService.getById(orderId);
        if (order == null) return Result.fail("订单不存在");
        if (OrderStatus.CLOSED == order.getStatus()) return Result.fail("订单已经关闭");
        if (!Objects.equals(order.getUserId(), RequestUtil.getUserId(request))){
            return Result.fail("这不是你的订单");
        }
        if (PayType.OFFLINE.equals(order.getPayType())){
            if(order.getStatus() != OrderStatus.READY) return Result.fail("到付订单进行中，无法取消");
        }else if(!List.of(
                OrderStatus.NOT_PAY,
                OrderStatus.REFUNDED,
                OrderStatus.RECEIVED,
                OrderStatus.REVIEWED
        ).contains(order.getStatus())){
            return Result.fail("交易未结束，暂时无法取消");
        }
        if (orderService.closeOrder(List.of(orderId))){
            return Result.success();
        }
        return Result.fail("取消失败");
    }

    @GetMapping("/details")
    @ResponseBody
    public Result<OrderVo> details(HttpServletRequest request, @RequestParam("id") List<Long> ids){
        List<OrderStoreVo> details = orderService.details(RequestUtil.getUserId(request), ids);
        if (details != null) return Result.success(
                new OrderVo(LocalDateTime.now(), new VoList<>(
                        details.size(),
                        details
                ))
        );
        return Result.fail("获取失败");
    }

}
