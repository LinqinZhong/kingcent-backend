package com.kingcent.common.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("kc_shop_order_goods")
public class OrderGoodsEntity {
    private Long id;
    private Long orderId;
    private Long userId;
    private Long skuId;
    private String title;
    private String skuInfo;
    private String thumbnail;
    private Integer count;
    private BigDecimal unitPrice;
    private BigDecimal price;
    private BigDecimal discount;
}
