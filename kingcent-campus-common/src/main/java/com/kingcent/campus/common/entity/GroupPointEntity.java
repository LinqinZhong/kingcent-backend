package com.kingcent.campus.common.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_shop_group_point")
public class GroupPointEntity {
    private Long id;
    private Long groupId;
    private String name;
    private Integer floor;
    private Boolean type;
    @TableLogic
    private Integer isDeleted;
}
