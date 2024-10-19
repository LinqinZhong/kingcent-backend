package com.kingcent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.shop.entity.DeliveryGroup;
import com.kingcent.common.shop.mapper.DeliveryGroupMapper;
import com.kingcent.service.DeliveryGroupService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class AppShopDeliveryGroupService extends ServiceImpl<DeliveryGroupMapper, DeliveryGroup> implements DeliveryGroupService {
}
