package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName("kc_agrc_damage")
@Node("Damage")
public class DamageEntity {
    @Id
    private Long id;
    @Property
    private String name;
    @Property
    private Integer type;
    @Property
    private Integer dangerLevel;
    private String description;
    private String thumb;
}
