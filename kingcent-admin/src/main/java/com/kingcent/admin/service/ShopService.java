package com.kingcent.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.common.entity.vo.VoList;
import com.kingcent.common.shop.entity.ShopEntity;
import com.kingcent.common.shop.entity.vo.shop.ShopNameVo;

import java.util.Collection;
import java.util.Map;

public interface ShopService extends IService<ShopEntity> {

    Map<Long, String> getShopNames(Collection<Long> shopIds);

    VoList<ShopNameVo> getShopNames(String keywords, Integer page);

    boolean exists(Long shopId);

    String getShopName(Long shopId);

    Result<VoList<ShopEntity>> list(Integer page, Integer pageSize);
}
