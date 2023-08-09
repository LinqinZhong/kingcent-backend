package com.kingcent.campus.shop.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.*;
import com.kingcent.campus.shop.entity.vo.cart.*;
import com.kingcent.campus.shop.entity.vo.purchase.PutCartGoodsVo;
import com.kingcent.campus.shop.mapper.ShopCartGoodsMapper;
import com.kingcent.campus.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
public class CartGoodsServiceImpl extends ServiceImpl<ShopCartGoodsMapper, CartGoodsEntity> implements CartGoodsService {

    @Override
    public Result<CartGoodsEntity> updateSku(Long userId, String cartGoodsCode, String specInfo) {
        return null;
    }

    @Override
    public Result<?> put(Long userId, PutCartGoodsVo vo) {
        return null;
    }

    @Override
    public CartVo listByUserId(Long userId) {
        return null;
    }

    @Override
    public Result<?> updateCount(Long userId, String cartGoodsCode, Integer count) {
        return null;
    }

    @Override
    public Result<?> updateCheck(Long userId, CartCheckVo check) {
        return null;
    }

    @Override
    public Result<?> delete(Long userId, List<String> cartGoodsCodes) {
        return null;
    }
}
