package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_agrc_variety")
public class VarietyEntity {
    private Long id;
    private String name;
}
