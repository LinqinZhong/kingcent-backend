package com.kingcent.campus.entity.vo.goods;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsVo {
    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String thumbnail;
    private List<String> tags;
}