package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.OrderRefundFailLogService;
import com.kingcent.campus.service.OrderRefundService;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.entity.OrderRefundEntity;
import com.kingcent.campus.shop.entity.OrderRefundFailLogEntity;
import com.kingcent.campus.shop.entity.vo.refund.RefundFailLogVo;
import com.kingcent.campus.shop.entity.vo.refund.RefundInfoVo;
import com.kingcent.campus.shop.mapper.OrderRefundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:49
 */
@Service
public class AppRefundOrderService extends ServiceImpl<OrderRefundMapper, OrderRefundEntity> implements OrderRefundService {


    @Lazy
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRefundFailLogService failLogService;

    @Override
    public Result<RefundInfoVo> getRefundInfo(Long userId, Long orderId) {
        OrderEntity order = orderService.getOne(
                new QueryWrapper<OrderEntity>()
                        .eq("id", orderId)
                        .eq("user_id", userId)
        );
        if (order == null) return Result.fail("订单不存在");
        OrderRefundEntity refund = getOne(new QueryWrapper<OrderRefundEntity>()
                .eq("order_id", orderId)
                .last("limit 1")
        );
        if(refund == null) return Result.fail("退款信息不存在");
        List<OrderRefundFailLogEntity> logs = failLogService.list(new QueryWrapper<OrderRefundFailLogEntity>()
                .eq("order_id", orderId)
        );
        RefundInfoVo vo = new RefundInfoVo();
        vo.setOrderNo(order.getOrderNo());
        vo.setTradeNo(order.getTradeNo());
        vo.setPrice(order.getPrice());
        vo.setStatus(order.getStatus());
        vo.setRefundTime(refund.getRefundTime());
        vo.setPayType(order.getPayType());
        vo.setMessage(refund.getMessage());
        vo.setReason(refund.getReason());
        vo.setPayTime(order.getPayTime());
        vo.setCreateTime(refund.getCreateTime());
        if(logs.size() > 0){
            List<RefundFailLogVo> logVos = new ArrayList<>();
            for (OrderRefundFailLogEntity log : logs) {
                logVos.add(new RefundFailLogVo(
                        log.getMessage(),
                        log.getTime(),
                        log.getType()
                ));
            }
            vo.setFailLogs(logVos);
        }
        return Result.success(vo);
    }
}
