package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.service.OrderRefundFailLogService;
import com.kingcent.campus.service.OrderRefundMapService;
import com.kingcent.campus.service.OrderRefundService;
import com.kingcent.campus.service.OrderService;
import com.kingcent.campus.shop.entity.OrderRefundEntity;
import com.kingcent.campus.shop.entity.OrderRefundFailLogEntity;
import com.kingcent.campus.shop.entity.OrderRefundMapEntity;
import com.kingcent.campus.shop.entity.vo.refund.RefundFailLogVo;
import com.kingcent.campus.shop.entity.vo.refund.RefundInfoVo;
import com.kingcent.campus.shop.mapper.OrderRefundMapper;
import com.kingcent.campus.wx.service.WxRefundService;
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
public class AppOrderRefundService extends ServiceImpl<OrderRefundMapper, OrderRefundEntity> implements OrderRefundService {


    @Lazy
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRefundFailLogService failLogService;

    @Autowired
    private OrderRefundMapService refundMapService;

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
