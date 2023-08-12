package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("kc_shop_goods")
public class GoodsEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long shopId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Long skuId;
    private String thumbnail;
    private String images;
    private String description;
    private Date createTime;
    private Date updateTime;

    private Integer isSale;

    @TableLogic
    private Integer isDeleted;
}
