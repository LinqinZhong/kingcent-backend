package com.kingcent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.shop.entity.OrderRefundEntity;
import com.kingcent.common.shop.entity.vo.refund.RefundInfoVo;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:48
 */
public interface OrderRefundService extends IService<OrderRefundEntity> {
    Result<Integer> cancel(Long userId, Long orderId);

    Result<RefundInfoVo> getRefundInfo(Long userId, Long orderId);
}
