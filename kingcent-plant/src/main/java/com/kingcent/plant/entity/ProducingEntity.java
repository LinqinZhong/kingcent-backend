package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_agrc_producing")
public class ProducingEntity {
    private Long id;
    private String name;
    private String description;
    private String thumb;
}
