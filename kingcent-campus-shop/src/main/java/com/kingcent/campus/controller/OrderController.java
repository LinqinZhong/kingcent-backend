package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.service.OrderRefundService;
import com.kingcent.campus.shop.constant.OrderStatus;
import com.kingcent.campus.shop.constant.PayType;
import com.kingcent.campus.shop.constant.RefundReasons;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.RefundReasonEntity;
import com.kingcent.campus.shop.entity.vo.order.OrderStoreVo;
import com.kingcent.campus.shop.entity.vo.order.OrderVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.shop.entity.vo.refund.RefundInfoVo;
import com.kingcent.campus.shop.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRefundService refundOrderService;

    @PostMapping("/confirm")
    @ResponseBody
    public Result<?> confirmOrder(HttpServletRequest request, @RequestBody PurchaseConfirmVo confirmVo){
        return orderService.createOrders(RequestUtil.getUserId(request), RequestUtil.getLoginId(request), confirmVo, RequestUtil.getIpAddress(request), PayType.WX_PAY);
    }

    @GetMapping("/pay/{orderId}")
    @ResponseBody Result<?> pay(HttpServletRequest request, @PathVariable Long orderId){
        return orderService.pay(RequestUtil.getUserId(request), RequestUtil.getLoginId(request),orderId, RequestUtil.getIpAddress(request));
    }

    @GetMapping("/list/{page}")
    @ResponseBody
    public Result<VoList<OrderStoreVo>> list(HttpServletRequest request, @PathVariable Integer page, @RequestParam(required = false) Integer status){
        VoList<OrderStoreVo> res = orderService.orderList(RequestUtil.getUserId(request), status, page);
        return Result.success(res);
    }

    @GetMapping("/refund_reasons")
    public Result<List<RefundReasonEntity>> refundReasons(){
        return Result.success(RefundReasons.list);
    }

    @GetMapping("/close/{orderId}")
    @ResponseBody
    @Transactional
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
                OrderStatus.REVIEWED,
                OrderStatus.ARRIVED
        ).contains(order.getStatus())){
            return Result.fail("交易未结束，暂时无法取消");
        }
        if (orderService.removeCloseOrderTask(Set.of(orderId+"")) && orderService.closeOrder(List.of(orderId))){
            return Result.success();
        }
        return Result.fail("取消失败");
    }

    /**
     * 修改取货码
     */
    @PostMapping("/set_receive_code")
    @ResponseBody
    public Result<String> setReceiveCode(HttpServletRequest request,Long orderId,String code){
        return orderService.setReceiveCode(RequestUtil.getUserId(request),orderId,code);
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

    /**
     * 用户申请退货
     */
    @PostMapping("/refund")
    public Result<?> requireRefund(
            HttpServletRequest request,
            @RequestParam Long orderId,
            @RequestParam Integer reason,
            @RequestParam String message
    ){
        return orderService.requireRefund(
                RequestUtil.getUserId(request),
                RequestUtil.getLoginId(request),
                orderId,
                reason,
                message
        );
    }

    /**
     * 获取订单退货信息
     */
    @GetMapping("/refund_info")
    public Result<RefundInfoVo> getRefundInfo(HttpServletRequest request, @RequestParam Long orderId){
        return refundOrderService.getRefundInfo(RequestUtil.getUserId(request), orderId);
    }

    @GetMapping("/cancel_refund/{orderId}")
    @ResponseBody
    public Result<Integer> cancelRefund(HttpServletRequest request, @PathVariable Long orderId) {
        return refundOrderService.cancel(RequestUtil.getUserId(request), orderId);
    }

}
