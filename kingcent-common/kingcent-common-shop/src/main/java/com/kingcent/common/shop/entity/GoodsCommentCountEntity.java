package com.kingcent.common.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@TableName("kc_shop_goods_comment_count")
@EqualsAndHashCode(callSuper = false)
public class GoodsCommentCountEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("goods_id")
    private Long goodsId;

    @TableField("good")
    private Integer good;

    private Integer mid;

    private Integer bad;

    private Integer hasImage;

    @TableField("is_deleted")
    private Integer isDeleted;
}
