package com.kingcent.campus.shop.entity.vo.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartGoodsVo {
    private Long goodsId;
    private Boolean checked;
    private String thumb;
    private String defaultThumb;
    private String title;
    private String skuInfo;
    private String sku;
    private BigDecimal unitPrice;
    private Integer count;
    private List<CartGoodsDiscountVo> discount;
}
