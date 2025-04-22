package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.PesticideEntity;
import com.kingcent.plant.mapper.PesticideMapper;
import com.kingcent.plant.repository.DamageRepository;
import com.kingcent.plant.repository.PesticideRepository;
import com.kingcent.plant.service.PesticideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.repository.query.QueryFragmentsAndParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class PesticideServiceImpl extends ServiceImpl<PesticideMapper, PesticideEntity> implements PesticideService {

    @Autowired
    private Neo4jTemplate neo4jTemplate;
    @Autowired
    private DamageRepository damageRepository;
    @Autowired
    private PesticideRepository pesticideRepository;
    @Override
    public Page<PesticideEntity> getPage(Integer pageNum, Integer pageSize){
        Page<PesticideEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(PesticideEntity entity){
        saveOrUpdate(entity);
        pesticideRepository.save(entity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        pesticideRepository.deleteById(id);
        return Result.success();
    }

    @Override
    public void addPesticide(Long id, Long damageId) throws KingcentSystemException {
        Optional<DamageEntity> damageRes = damageRepository.findById(id);
        Optional<PesticideEntity> pesticideRes = pesticideRepository.findById(id);
        if(damageRes.isEmpty() || pesticideRes.isEmpty()){
            throw  new KingcentSystemException("不存在");
        }
        pesticideRepository.treat(id, damageId, 1);
        damageRepository.treatedBy(damageId, id, 1);
    }

    @Override
    public List<DamageEntity> getTreat(Long id){
        QueryFragmentsAndParameters parameters = new QueryFragmentsAndParameters(
                "MATCH p=(pesticide: Pesticide{id:$id})-[r:treat]->(damage: Damage) " +
                        "RETURN COLLECT(damage) as damages",
                Map.of("id",id)
        );
        return neo4jTemplate.toExecutableQuery(DamageEntity.class, parameters).getResults();
    }

    @Override
    public void deleteTreat(Long id, Long damageId){
        pesticideRepository.deleteTreat(id, damageId, 1);
        damageRepository.deleteTreatedBy(damageId, id, 1);
    }
}
