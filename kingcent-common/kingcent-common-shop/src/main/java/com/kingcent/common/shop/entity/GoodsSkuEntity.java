package com.kingcent.common.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("kc_shop_goods_sku")
public class GoodsSkuEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal cost;
    private Integer limitMinCount;
    private Integer limitMaxCount;
    private String image;
    private String specInfo;
    private Integer stockQuantity;
    private Integer safeStockQuantity;
    private Integer soldQuantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}