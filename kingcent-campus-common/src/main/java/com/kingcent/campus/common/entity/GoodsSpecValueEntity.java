package com.kingcent.campus.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("kc_shop_goods_spec_value")
public class GoodsSpecValueEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private Long specId;
    private String val;
    private String image;

    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer isDeleted;
}
