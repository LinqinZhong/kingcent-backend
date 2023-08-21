package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.*;
import com.kingcent.campus.shop.constant.OrderStatus;
import com.kingcent.campus.shop.constant.RefundStatus;
import com.kingcent.campus.shop.entity.*;
import com.kingcent.campus.shop.entity.vo.refund.RefundFailLogVo;
import com.kingcent.campus.shop.entity.vo.refund.RefundInfoVo;
import com.kingcent.campus.shop.mapper.OrderRefundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:49
 */
@Service
public class AppOrderRefundService extends ServiceImpl<OrderRefundMapper, OrderRefundEntity> implements OrderRefundService {


    @Lazy
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRefundFailLogService failLogService;

    @Autowired
    private OrderRefundMapService refundMapService;

    @Autowired
    private DeliveryTemplateService deliveryTemplateService;

    @Transactional
    @Override
    public Result<?> cancel(Long userId, Long orderId){
        //查找对应的订单
        OrderEntity order = orderService.getById(orderId);
        if(order == null || !order.getUserId().equals(userId)){
            return Result.fail("订单不存在");
        }
        if(!order.getStatus().equals(OrderStatus.REFUNDING)){
            return Result.fail("该订单没有发起退款");
        }
        //查找对应的退款订单
        OrderRefundMapEntity orderRefundBind = refundMapService.getOne(
                new QueryWrapper<OrderRefundMapEntity>()
                        .eq("order_id", orderId)
        );
        if(orderRefundBind == null){
            return Result.fail("该订单没有发起退款");
        }
        //查找对应的退款订单
        OrderRefundEntity refund = getById(orderRefundBind.getRefundId());
        if(refund == null){
            return Result.fail("退款订单不存在");
        }
        //如果订单未配送，当前时间超过配送时间或距离配送时间小于预留时间的无法取消退款申请
        if(refund.getOriginOrderStatus().equals(OrderStatus.READY) || refund.getOriginOrderStatus().equals(OrderStatus.DELIVERING)){
            if(LocalDateTime.now().isAfter(order.getDeliveryTime())){
                return Result.fail("配送时间已过，该笔订单只能退款");
            }
            //查找店铺配送时间
            DeliveryTemplateEntity plt = deliveryTemplateService.getOne(
                    new QueryWrapper<DeliveryTemplateEntity>()
                            .eq("shop_id", order.getShopId())
                            .eq("is_used", true)
                            .last("limit 1")
            );
            if (plt == null){
                return Result.fail("店铺已经休息，该笔订单只能退款");
            }
            if(LocalDateTime.now().isAfter(order.getDeliveryTime().minusMinutes(plt.getReserveTime()+30))){
                return Result.fail("距离配送时间少于店铺预留下单时间（"+parseMinute(plt.getReserveTime()+30)+"），该笔订单只能退款");
            }
        }
        //更新订单状态
        if (!orderService.update(new UpdateWrapper<OrderEntity>()
                .eq("id", orderId)
                .eq("user_id", userId)
                .eq("status", OrderStatus.REFUNDING)
                .set("status", refund.getOriginOrderStatus())
        ) || !update(new UpdateWrapper<OrderRefundEntity>()
                .eq("id", refund.getId())
                .eq("status", refund.getStatus())
                .set("status", RefundStatus.CANCEL)
        )) {
            return Result.busy();
        }
        return Result.success();
    }

    private String parseMinute(long m){
        String res = "";
        if(m > 1440){
            long d = m/1440;
            m -= d*1440;
            res += d+"天";
        }
        if(m > 60){
            long h = m/60;
            m -= h*60;
            res += h+"小时";
        }
        if(m > 0) res += m+"分钟";
        return res;
    }

    @Override
    public Result<RefundInfoVo> getRefundInfo(Long userId, Long orderId) {
        //查找对应的退款订单
        OrderRefundMapEntity orderRefundBind = refundMapService.getOne(
                new QueryWrapper<OrderRefundMapEntity>()
                        .eq("order_id", orderId)
        );
        if(orderRefundBind == null){
            return Result.fail("该订单没有发起退款");
        }
        //查找对应的退款订单
        OrderRefundEntity refund = getById(orderRefundBind.getRefundId());
        if(refund == null){
            return Result.fail("退款订单不存在");
        }

        List<OrderRefundFailLogEntity> logs = failLogService.list(
                new QueryWrapper<OrderRefundFailLogEntity>()
                .eq("refund_id", refund.getId())
        );
        RefundInfoVo vo = new RefundInfoVo();
        vo.setOutRefundNo(refund.getOutRefundNo());
        vo.setRefundNo(refund.getRefundNo());
        vo.setPrice(refund.getRefund());
        vo.setStatus(refund.getStatus());
        vo.setRefundTime(refund.getRefundTime());
        vo.setPayType(refund.getPayType());
        vo.setMessage(refund.getMessage());
        vo.setReason(refund.getReason());
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
