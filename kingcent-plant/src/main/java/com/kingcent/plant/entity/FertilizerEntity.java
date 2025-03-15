package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_agrc_fertilizer")
public class FertilizerEntity {
    private Long id;
    private String name;
    private String description;
}
