package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.OrderRefundEntity;
import com.kingcent.campus.shop.entity.vo.refund.RefundInfoVo;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:48
 */
public interface OrderRefundService extends IService<OrderRefundEntity> {
    Result<Integer> cancel(Long userId, Long orderId);

    Result<RefundInfoVo> getRefundInfo(Long userId, Long orderId);
}
