package com.kingcent.campus.admin.controller;

import cn.hutool.db.sql.Order;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.admin.entity.vo.RefundOrderVo;
import com.kingcent.campus.admin.service.OrderRefundService;
import com.kingcent.campus.admin.service.OrderService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.vo.order.OrderVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author zzy
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRefundService orderRefundService;

    @DeleteMapping("/delete/{shopId}/{orderId}")
    public Result<?> delete(@PathVariable Long shopId, @PathVariable Long orderId){
        if(orderService.remove(
                new QueryWrapper<OrderEntity>()
                        .eq("shop_id", shopId)
                        .eq("id", orderId)
        )){
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    @PostMapping("/confirm_refund")
    public Result<?> confirmRefund(Long shopId, Long refundId, BigDecimal price) {
        return orderRefundService.confirmRefund(shopId, refundId,price);
    }

    @GetMapping("/list/{page}/{pageSize}")
    public Result<VoList<OrderEntity>> list(
            @PathVariable Integer page,
            @PathVariable Integer pageSize,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Integer status
    ){
        return orderService.list(page, pageSize, shopId);
    }

    @PutMapping("/set_discount/{shopId}/{orderId}/{discount}")
    public Result<?> setDiscount(
            @PathVariable Long shopId,
            @PathVariable Long orderId,
            @PathVariable BigDecimal discount
    ){
        return orderService.setDiscount(shopId, orderId, discount);
    }


    @PostMapping("/agree_refund")
    public Result<?> allow(Long shopId, Long refundId){
        return orderRefundService.agree(shopId, refundId);
    }

    @GetMapping("/refund_list/{shopId}/{pageNum}")
    public Result<VoList<RefundOrderVo>> refundList(
            HttpServletRequest request,
            @PathVariable Long shopId,
            @PathVariable Integer pageNum,
            @RequestParam(required = false) Integer status
    ){
        return Result.success(orderRefundService.refundList(shopId, pageNum, status));
    }

//    @PostMapping("/start_delivery/{groupId}/{shopId}/{pageNum}")
//    public Result<List<OrderEntity>> startDelivery(@PathVariable Long groupId, @PathVariable Long shopId, @PathVariable Integer count){
//        return orderService.startDelivery(groupId, shopId, count, LocalDate.now());
//    }


}
