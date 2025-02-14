package com.kingcent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.common.shop.entity.OrderDeliveryEntity;

/**
 * @author rainkyzhong
 * @date 2023/8/24 20:02
 */
public interface OrderDeliveryService extends IService<OrderDeliveryEntity> {
    Result<?> finish(Long userId, Long orderId, String code);
}
