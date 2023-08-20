package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.service.OrderRefundMapService;
import com.kingcent.campus.shop.entity.OrderRefundMapEntity;
import com.kingcent.campus.shop.mapper.OrderRefundMapMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/20 20:51
 */
@Service
public class AppOrderRefundMapService extends ServiceImpl<OrderRefundMapMapper, OrderRefundMapEntity> implements OrderRefundMapService {
}
