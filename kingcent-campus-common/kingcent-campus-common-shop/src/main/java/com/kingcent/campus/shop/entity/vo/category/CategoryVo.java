package com.kingcent.campus.shop.entity.vo.category;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CategoryVo {
    private Long id;
    private String name;
    private String thumbnail;
    private List<CategoryVo> children;
    private String ref;
    private BigDecimal price;
    private Integer sales;
}
