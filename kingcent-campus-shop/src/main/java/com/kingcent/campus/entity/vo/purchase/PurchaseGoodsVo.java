package com.kingcent.campus.entity.vo.purchase;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseGoodsVo {
    private Long id;
    private String thumbnail;
    private String title;
    private Integer count;
    private String sku;
    private String skuDesc;
    private BigDecimal price;
}
