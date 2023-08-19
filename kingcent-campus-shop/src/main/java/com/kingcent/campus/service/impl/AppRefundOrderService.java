package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.service.RefundOrderService;
import com.kingcent.campus.shop.entity.RefundOrderEntity;
import com.kingcent.campus.shop.mapper.RefundOrderMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:49
 */
@Service
public class AppRefundOrderService extends ServiceImpl<RefundOrderMapper, RefundOrderEntity> implements RefundOrderService {
}
