package com.kingcent.common.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_shop_group_point_path")
public class GroupPointPathEntity {
    private Long point1;
    private Long point2;
    private String passPoints;
    private Integer cost;
}
