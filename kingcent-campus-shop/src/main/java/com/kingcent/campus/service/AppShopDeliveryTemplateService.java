package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.DeliveryTemplateEntity;
import com.kingcent.campus.shop.mapper.DeliveryTemplateMapper;
import com.kingcent.campus.shop.service.DeliveryTemplateService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class AppShopDeliveryTemplateService extends ServiceImpl<DeliveryTemplateMapper, DeliveryTemplateEntity> implements DeliveryTemplateService {
}
