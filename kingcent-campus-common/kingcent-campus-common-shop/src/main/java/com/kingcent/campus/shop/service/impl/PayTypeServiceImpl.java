package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.PayTypeEntity;
import com.kingcent.campus.shop.mapper.PayTypeMapper;
import com.kingcent.campus.shop.service.PayTypeService;
import org.springframework.stereotype.Service;

@Service
public class PayTypeServiceImpl extends ServiceImpl<PayTypeMapper, PayTypeEntity> implements PayTypeService {
}
