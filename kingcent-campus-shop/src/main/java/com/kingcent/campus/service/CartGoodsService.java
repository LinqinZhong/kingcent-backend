package com.kingcent.campus.service;

import com.kingcent.campus.common.entity.CartGoodsEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.cart.CartCheckVo;
import com.kingcent.campus.entity.vo.cart.CartVo;
import com.kingcent.campus.entity.vo.purchase.PutCartGoodsVo;

import java.util.List;

public interface CartGoodsService{
    Result<CartGoodsEntity> updateSku(Long userId, String cartGoodsCode, String specInfo);

    Result<?> put(Long userId, PutCartGoodsVo vo);

    CartVo listByUserId(Long userId);

    Result<?> updateCount(Long userId, String cartGoodsCode, Integer count);

    Result<?> updateCheck(Long userId, CartCheckVo check);

    Result<?> delete(Long userId, List<String> cartGoodsCodes);
}
