package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.ArrayList;
import java.util.List;

@Data
@TableName("kc_agrc_pesticide")
@Node("Pesticide")
public class PesticideEntity {
    @Id
    private Long id;
    @Property
    private String name;
    @Property
    private String react;
    @Property
    private String element;
    @Property
    private String reason;
    @Property
    private String reactTime;
    @Property
    private String duration;

    private String description;
    private String thumb;
}
