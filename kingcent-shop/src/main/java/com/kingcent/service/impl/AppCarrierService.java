package com.kingcent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.service.CarrierService;
import com.kingcent.common.shop.entity.CarrierEntity;
import com.kingcent.common.shop.mapper.CarrierMapper;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2023/8/24 19:19
 */
@Service
public class AppCarrierService extends ServiceImpl<CarrierMapper, CarrierEntity> implements CarrierService {
}
