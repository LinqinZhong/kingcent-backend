package com.kingcent.campus.common.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName("kc_shop_site")
@Data
public class SiteEntity {
    private Long id;
    private String name;
    private Double longitude;
    private Double latitude;
    @TableLogic
    private Integer isDeleted;
}