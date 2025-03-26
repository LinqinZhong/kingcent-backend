package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kc_agrc_pesticide")
public class PesticideEntity {
    private Long id;
    private String name;
    private String description;
    private String thumb;
    private String react;
    private String element;
    private String reason;
    private String reactTime;
    private String duration;
}
