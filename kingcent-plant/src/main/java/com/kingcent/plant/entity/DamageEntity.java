package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_agrc_damage")
public class DamageEntity {
    private Long id;
    private String name;
    private String description;
}
