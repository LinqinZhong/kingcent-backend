package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.service.OrderOutTradeService;
import com.kingcent.campus.shop.entity.OrderOutTradeEntity;
import com.kingcent.campus.shop.mapper.OrderOutTradeMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/23 5:18
 */
@Service
public class AppOrderOutTradeService extends ServiceImpl<OrderOutTradeMapper,OrderOutTradeEntity> implements OrderOutTradeService {
}
