package com.kingcent.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.admin.entity.vo.RefundOrderVo;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.OrderRefundEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:48
 */
public interface OrderRefundService extends IService<OrderRefundEntity> {
    @Transactional
    Result<?> confirmRefund(Long shopId, Long refundId, BigDecimal price);

    VoList<RefundOrderVo> refundList(Long shopId, Integer pageNum, Integer status);

    Result<?> agree(Long shopId, Long refundId);
}
