package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("kc_shop_goods_discount")
public class GoodsDiscountEntity {
    private Long id;
    private Long shopId;
    private Long goodsId;
    private Integer type;
    private BigDecimal moreThan;
    private BigDecimal num;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime deadline;
    private Integer isDeleted;
}
