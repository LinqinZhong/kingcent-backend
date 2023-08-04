package com.kingcent.campus.common.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@TableName("kc_shop")
@Data
public class ShopEntity {
    private Long id;
    private String name;
    private Integer status;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDeleted;
}
