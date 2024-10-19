package com.kingcent.common.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@TableName("kc_shop_goods")
public class GoodsEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long shopId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String thumbnail;
    private String images;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isSale;
    @TableField(exist = false)
    private Integer sales;
    @TableLogic
    private Integer isDeleted;
}
