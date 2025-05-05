package com.kingcent.plant.repository;

import com.kingcent.plant.entity.VarietyEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VarietyRepository extends Neo4jRepository<VarietyEntity,Long> {

    @Query("MATCH (pesticide:Pesticide {id: $pesticideId}), (damage:Damage {id: $damageId}) " +
            "MERGE (damage)-[r2:treatedBy]->(pesticide) " +
            "ON CREATE SET r2.rating = $rating"
    )
    void treatedBy(Long damageId, Long pesticideId, Integer rating);

    @Query("MATCH (pesticide:Pesticide {id: $pesticideId}) -[r:treatedBy]-> (damage:Damage {id: $damageId}) DELETE r")
    void deleteTreatedBy(Long damageId, Long pesticideId, Integer rating);
}
