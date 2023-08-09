package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.ShopEntity;
import com.kingcent.campus.shop.mapper.ShopMapper;
import com.kingcent.campus.shop.service.ShopService;

import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
public class ShopServiceImpl extends ServiceImpl<ShopMapper, ShopEntity> implements ShopService {

    @Override
    public Map<Long, String> shopNamesMap(Collection<Long> shopIds) {
        return null;
    }
}
