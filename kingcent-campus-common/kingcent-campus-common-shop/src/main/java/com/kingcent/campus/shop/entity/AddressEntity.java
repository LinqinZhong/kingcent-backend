package com.kingcent.campus.shop.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_shop_address")
public class AddressEntity {
    private Long id;
    private Long userId;
    private Long siteId;
    private Long groupId;
    private Long pointId;
    private String name;
    private String mobile;
    private Integer gender;
    private Boolean isDefault;
    @TableLogic
    private Boolean isDeleted;
}
