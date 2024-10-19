package com.kingcent.common.shop.entity;

import lombok.Data;

@Data
public class CartGoodsEntity {
    private Long goodsId;
    private String sku;
    private Integer count;
    private Integer checked;
}
