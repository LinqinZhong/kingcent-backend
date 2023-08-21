package com.kingcent.campus.admin.controller;

import com.kingcent.campus.admin.entity.vo.RefundOrderVo;
import com.kingcent.campus.admin.service.OrderRefundService;
import com.kingcent.campus.admin.service.OrderService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

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

    @PostMapping("/confirm_refund")
    public Result<?> confirmRefund(Long shopId, Long refundId, BigDecimal price) {
        return orderRefundService.confirmRefund(shopId, refundId,price);
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
}
