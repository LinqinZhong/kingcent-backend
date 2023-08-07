package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_shop_order")
public class OrderEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
}
