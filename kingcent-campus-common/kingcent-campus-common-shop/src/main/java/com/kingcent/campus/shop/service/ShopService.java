package com.kingcent.campus.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.ShopEntity;

import java.util.Collection;
import java.util.Map;

public interface ShopService extends IService<ShopEntity> {

    Map<Long, String> shopNamesMap(Collection<Long> shopIds);
}
