package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_agrc_plant_base")
public class PlantBaseEntity {
    private Long id;
    private String name;
    private String description;
    private String thumb;
}
