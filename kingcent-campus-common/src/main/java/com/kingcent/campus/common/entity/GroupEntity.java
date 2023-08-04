package com.kingcent.campus.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
@TableName("kc_shop_group")
public class GroupEntity {

    @TableId
    private Long id;

    private String name;

    @TableField("site_id")
    private Long siteId;
    private Double longitude;
    private Double latitude;
    @TableLogic
    private Integer isDeleted;
}
