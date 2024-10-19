package com.kingcent.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.service.OrderGoodsService;
import com.kingcent.common.shop.entity.OrderGoodsEntity;
import com.kingcent.common.shop.mapper.OrderGoodsMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminOrderGoodsService extends ServiceImpl<OrderGoodsMapper, OrderGoodsEntity> implements OrderGoodsService {
}