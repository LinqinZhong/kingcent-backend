package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.service.RefundOrderMapService;
import com.kingcent.campus.shop.entity.RefundOrderMapEntity;
import com.kingcent.campus.shop.mapper.RefundOrderMapMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/19 5:49
 */
@Service
public class AppRefundOrderMapService extends ServiceImpl<RefundOrderMapMapper, RefundOrderMapEntity> implements RefundOrderMapService {
}
