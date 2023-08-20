package com.kingcent.campus.admin.controller;

import com.kingcent.campus.admin.service.OrderRefundService;
import com.kingcent.campus.admin.service.OrderService;
import com.kingcent.campus.common.entity.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

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
    public Result<?> confirmRefund(Long shopId, Long orderId, BigDecimal price) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        return orderRefundService.confirmRefund(shopId, orderId,price);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteOrder(@PathVariable Long id){
        return orderService.deleteOrder(id);
    }

}
