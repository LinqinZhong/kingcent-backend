package com.kingcent.campus.controller;

import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.campus.shop.service.OrderService;
import com.kingcent.campus.shop.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
