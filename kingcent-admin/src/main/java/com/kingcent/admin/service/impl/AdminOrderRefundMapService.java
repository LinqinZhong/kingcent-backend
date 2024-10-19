package com.kingcent.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.admin.service.OrderRefundMapService;
import com.kingcent.common.shop.entity.OrderRefundMapEntity;
import com.kingcent.common.shop.mapper.OrderRefundMapMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/20 20:51
 */
@Service
public class AdminOrderRefundMapService extends ServiceImpl<OrderRefundMapMapper, OrderRefundMapEntity> implements OrderRefundMapService {
}
