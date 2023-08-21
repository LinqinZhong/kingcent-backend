package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.OrderService;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 */
@Service
@Slf4j
public class AdminOrderService extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

}
