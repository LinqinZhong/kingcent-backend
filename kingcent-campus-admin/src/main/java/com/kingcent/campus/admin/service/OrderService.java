package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.OrderEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrderService extends IService<OrderEntity> {
    Result<VoList<OrderEntity>> list(Integer page, Integer pageSize, Long shopId);

    Result<?> setDiscount(Long shopId, Long orderId, BigDecimal discount);
//    @Transactional
//    Result<List<OrderEntity>> startDelivery(Long groupId, Long shopId, Integer count, LocalDate date);
}
