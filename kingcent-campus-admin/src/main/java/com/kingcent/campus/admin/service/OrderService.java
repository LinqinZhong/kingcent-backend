package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.OrderEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface OrderService extends IService<OrderEntity> {
//    @Transactional
//    Result<List<OrderEntity>> startDelivery(Long groupId, Long shopId, Integer count, LocalDate date);
}
