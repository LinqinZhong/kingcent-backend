package com.kingcent.campus.shop.entity.vo.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartVo {
    /**
     * 商铺
     */
    private List<CartStoreVo> stores;
}
