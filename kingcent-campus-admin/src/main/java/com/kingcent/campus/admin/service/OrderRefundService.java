package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.OrderRefundEntity;
import com.kingcent.campus.shop.entity.vo.refund.RefundInfoVo;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:48
 */
public interface OrderRefundService extends IService<OrderRefundEntity> {
    @Transactional
    Result<?> confirmRefund(Long shopId, Long refundId, BigDecimal price);
}
