package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.PayTypeEntity;
import com.kingcent.campus.shop.mapper.PayTypeMapper;
import com.kingcent.campus.service.PayTypeService;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class AppPayTypeService extends ServiceImpl<PayTypeMapper,PayTypeEntity> implements PayTypeService {
}
