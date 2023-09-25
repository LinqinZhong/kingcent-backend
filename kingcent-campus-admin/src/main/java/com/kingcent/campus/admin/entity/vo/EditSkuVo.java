package com.kingcent.campus.admin.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author rainkyzhong
 * @date 2023/8/28 14:03
 */
@Data
public class EditSkuVo {
    private String description;
    private String image;
    private String specInfo;
    private Integer stockQuantity;
    private BigDecimal cost;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer limitMinCount;
    private Integer limitMaxCount;
}
