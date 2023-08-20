package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.service.OrderRefundFailLogService;
import com.kingcent.campus.shop.entity.OrderRefundFailLogEntity;
import com.kingcent.campus.shop.mapper.OrderRefundFailLogMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:49
 */
@Service
public class AppOrderRefundFailLogService extends ServiceImpl<OrderRefundFailLogMapper, OrderRefundFailLogEntity> implements OrderRefundFailLogService {
}
