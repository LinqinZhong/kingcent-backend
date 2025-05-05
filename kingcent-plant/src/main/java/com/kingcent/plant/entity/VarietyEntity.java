package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@TableName("kc_agrc_variety")
@Node
public class VarietyEntity {
    @Id
    @TableId(type = IdType.AUTO)
    private Long id;
    @Property
    private String name;
    @Property
    private String description;
    @Property
    private String thumb;
}
