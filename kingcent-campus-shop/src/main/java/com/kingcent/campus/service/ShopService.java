package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.ShopEntity;
import com.kingcent.campus.shop.entity.vo.shop.ShopNameVo;

import java.util.Collection;
import java.util.Map;

public interface ShopService extends IService<ShopEntity> {

    Map<Long, String> shopNamesMap(Collection<Long> shopIds);

    boolean exists(Long shopId);
}
