package com.kingcent.campus.shop.entity.vo.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author rainkyzhong
 * @date 2023/8/11 7:28
 */
@Data
public class OrderGoodsVo {
    private String thumb;
    private String title;
    private String skuInfo;
    private BigDecimal price;
    private Integer count;
}
