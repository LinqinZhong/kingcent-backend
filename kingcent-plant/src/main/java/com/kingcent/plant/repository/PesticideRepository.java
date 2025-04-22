package com.kingcent.plant.repository;

import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.DamagePesticideEntity;
import com.kingcent.plant.entity.PesticideDamageEntity;
import com.kingcent.plant.entity.PesticideEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PesticideRepository extends Neo4jRepository<PesticideEntity,Long> {
    @Query("MATCH (pesticide:Pesticide {id: $pesticideId}), (damage:Damage {id: $damageId}) " +
            "MERGE (pesticide)-[r1:treat]->(damage) " +
            "ON CREATE SET r1.rating = $rating"
    )
    void treat(Long pesticideId, Long damageId, Integer rating);

    @Query("MATCH (pesticide:Pesticide {id: $pesticideId}) -[r]-> (damage:Damage {id: $damageId}) DELETE r")
    void deleteTreat(Long pesticideId, Long damageId, Integer rating);
}
