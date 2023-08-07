package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("kc_shop_goods_spec")
public class GoodsSpecEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Long goodsId;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDeleted;
}
