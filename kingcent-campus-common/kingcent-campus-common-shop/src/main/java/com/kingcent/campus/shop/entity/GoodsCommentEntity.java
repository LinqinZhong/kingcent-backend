package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("kc_shop_goods_comment")
public class GoodsCommentEntity {
    @TableId(type = IdType.AUTO)
    private Long	id;
    private Long	goodsId;
    private Long	userId;
    private Long	shopId;
    private BigDecimal score;
    private String	val;
    private String	images;
    private Long	orderId;
    private Date createTime;
    private Date	updateTime;
    private String specInfo;

    @TableLogic
    private Integer	isDeleted;
}