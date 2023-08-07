package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.OrderEntity;
import com.kingcent.campus.shop.mapper.OrderMapper;
import com.kingcent.campus.shop.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {

}
