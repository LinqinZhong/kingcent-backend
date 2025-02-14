package com.kingcent.service;

import com.kingcent.common.shop.entity.CartGoodsEntity;
import com.kingcent.common.result.Result;
import com.kingcent.common.shop.entity.vo.cart.CartCheckVo;
import com.kingcent.common.shop.entity.vo.cart.CartVo;
import com.kingcent.common.shop.entity.vo.purchase.PurchaseConfirmVo;
import com.kingcent.common.shop.entity.vo.purchase.PutCartGoodsVo;

import java.util.List;

public interface CartGoodsService{
    Result<CartGoodsEntity> updateSku(Long userId, String cartGoodsCode, String specInfo);

    Result<?> put(Long userId, PutCartGoodsVo vo);

    CartVo listByUserId(Long userId);

    Result<?> updateCount(Long userId, String cartGoodsCode, Integer count);

    Result<?> updateCheck(Long userId, CartCheckVo check);

    Result<?> delete(Long userId, List<String> cartGoodsCodes);

    void removeGoods(Long userId, PurchaseConfirmVo purchase);
}
