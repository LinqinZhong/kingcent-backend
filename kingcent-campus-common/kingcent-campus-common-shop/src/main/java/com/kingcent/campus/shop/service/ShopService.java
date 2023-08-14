package com.kingcent.campus.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.ShopEntity;
import com.kingcent.campus.shop.entity.vo.shop.ShopNameVo;

import java.util.Collection;
import java.util.Map;

public interface ShopService extends IService<ShopEntity> {

    Map<Long, String> shopNamesMap(Collection<Long> shopIds);

    VoList<ShopNameVo> getShopNames(String keywords, Integer page);

    boolean exists(Long shopId);
}
