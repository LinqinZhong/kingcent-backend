package com.kingcent.common.shop.entity.vo.goods;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsSkuVo {
    private Long skuId;
    private String skuImage;
    private String specInfo;
    private Integer limitMaxCount;
    private Integer limitMinCount;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
}
