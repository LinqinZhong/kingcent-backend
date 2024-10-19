package com.kingcent.common.shop.entity.vo.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartStoreVo {
    /**
     * 店铺ID
     */
    private Long id;
    /**
     * 店铺名称
     */
    private String name;
    /**
     * 商品列表
     */
    private List<CartGoodsVo> goodsList;
}