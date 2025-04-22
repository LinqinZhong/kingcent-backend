package com.kingcent.plant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@TableName("kc_agrc_policy")
@Node("Policy")
public class PolicyEntity {
    @Id
    private Long id;
    @Property
    private String name;
    private String description;
    private String thumb;
}
