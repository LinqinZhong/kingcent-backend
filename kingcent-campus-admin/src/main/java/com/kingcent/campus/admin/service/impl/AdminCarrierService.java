package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.CarrierService;
import com.kingcent.campus.shop.entity.CarrierEntity;
import com.kingcent.campus.shop.mapper.CarrierMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/24 19:19
 */
@Service
public class AdminCarrierService extends ServiceImpl<CarrierMapper, CarrierEntity> implements CarrierService {
}
