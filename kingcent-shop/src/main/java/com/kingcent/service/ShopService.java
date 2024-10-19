package com.kingcent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.shop.entity.ShopEntity;

import java.util.Collection;
import java.util.Map;

public interface ShopService extends IService<ShopEntity> {

    Map<Long, String> shopNamesMap(Collection<Long> shopIds);

    boolean exists(Long shopId);
}
