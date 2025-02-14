package com.kingcent.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.OrderEntity;

import java.math.BigDecimal;

public interface OrderService extends IService<OrderEntity> {
    Result<VoList<OrderEntity>> list(Integer page, Integer pageSize, Long shopId);

    Result<?> setDiscount(Long shopId, Long orderId, BigDecimal discount);
//    @Transactional
//    Result<List<OrderEntity>> startDelivery(Long groupId, Long shopId, Integer count, LocalDate date);
}
