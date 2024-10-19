package com.kingcent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.service.OrderRefundFailLogService;
import com.kingcent.common.shop.entity.OrderRefundFailLogEntity;
import com.kingcent.common.shop.mapper.OrderRefundFailLogMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:49
 */
@Service
public class AppOrderRefundFailLogService extends ServiceImpl<OrderRefundFailLogMapper, OrderRefundFailLogEntity> implements OrderRefundFailLogService {
}
