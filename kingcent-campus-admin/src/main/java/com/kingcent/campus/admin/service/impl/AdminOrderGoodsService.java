package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.OrderGoodsService;
import com.kingcent.campus.shop.entity.OrderGoodsEntity;
import com.kingcent.campus.shop.mapper.OrderGoodsMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderGoodsService extends ServiceImpl<OrderGoodsMapper, OrderGoodsEntity> implements OrderGoodsService {
}