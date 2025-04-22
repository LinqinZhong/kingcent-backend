package com.kingcent.plant.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
public class PesticideDamageEntity {
    @RelationshipId
    private Long id;
    @TargetNode
    private DamageEntity damage;
}
