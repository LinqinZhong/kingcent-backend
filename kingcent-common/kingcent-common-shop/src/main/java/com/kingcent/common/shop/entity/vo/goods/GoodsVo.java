package com.kingcent.common.shop.entity.vo.goods;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class GoodsVo {
    private Long id;
    private Long shopId;
    private String shopName;
    private String name;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String thumbnail;
    private Integer sales;
    private LocalTime deliveryTime;
    private List<String> tags;
}