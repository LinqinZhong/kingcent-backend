package com.kingcent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.service.OrderPaymentService;
import com.kingcent.common.shop.entity.OrderPaymentEntity;
import com.kingcent.common.shop.mapper.OrderPaymentMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/24 4:16
 */
@Service
public class AppOrderPaymentService extends ServiceImpl<OrderPaymentMapper, OrderPaymentEntity> implements OrderPaymentService {
}
