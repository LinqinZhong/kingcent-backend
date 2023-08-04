package com.kingcent.campus.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("kc_shop_goods_discount")
public class GoodsDiscountEntity {
    private Long id;
    private Long goodsId;
    private Integer type;
    private BigDecimal moreThan;
    private BigDecimal num;
    private Date createTime;
    private Date updateTime;
    private Date deadline;
    private Integer isDeleted;
}
