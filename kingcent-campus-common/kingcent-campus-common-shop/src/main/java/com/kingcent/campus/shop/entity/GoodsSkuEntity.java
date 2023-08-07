package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
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
    private Integer limitMinCount;
    private Integer limitMaxCount;
    private String image;
    private String specInfo;
    private Integer stockQuantity;
    private Integer safeStockQuantity;
    private Integer soldQuantity;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDeleted;
}