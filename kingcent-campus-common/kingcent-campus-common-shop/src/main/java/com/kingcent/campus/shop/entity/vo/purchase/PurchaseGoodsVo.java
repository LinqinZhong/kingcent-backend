package com.kingcent.campus.shop.entity.vo.purchase;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseGoodsVo {
    private Long id;
    private String thumbnail;
    private String title;
    private Integer stock;
    private Integer countBought;
    private Boolean countIsReset;
    private Integer count;
    private String sku;
    private String skuDesc;
    private BigDecimal price;
}
