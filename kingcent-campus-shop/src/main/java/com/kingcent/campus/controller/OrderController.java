package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.vo.order.OrderStoreVo;
import com.kingcent.campus.shop.entity.vo.order.OrderVo;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.service.OrderService;
import com.kingcent.campus.shop.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
