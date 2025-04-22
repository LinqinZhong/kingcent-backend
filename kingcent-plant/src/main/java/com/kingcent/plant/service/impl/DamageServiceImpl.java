package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.PesticideEntity;
import com.kingcent.plant.mapper.DamageMapper;
import com.kingcent.plant.repository.DamageRepository;
import com.kingcent.plant.service.DamageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jTemplate;
import org.springframework.data.neo4j.repository.query.QueryFragmentsAndParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class DamageServiceImpl extends ServiceImpl<DamageMapper, DamageEntity> implements DamageService {

    @Autowired
    private DamageRepository damageRepository;

    @Autowired
    private Neo4jTemplate neo4jTemplate;

    @Override
    public Page<DamageEntity> getPage(Integer pageNum, Integer pageSize){
        Page<DamageEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(DamageEntity damageEntity){
        saveOrUpdate(damageEntity);
        damageRepository.save(damageEntity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        damageRepository.deleteById(id);
        return Result.success();
    }


    @Override
    public List<PesticideEntity> getTreatedBy(Long id){
        QueryFragmentsAndParameters parameters = new QueryFragmentsAndParameters(
                "MATCH p=(damage: Damage{id:$id})-[r:treated]->(pesticide: Pesticide) " +
                        "RETURN COLLECT(pesticide) as pesticides",
                Map.of("id",id)
        );
        return neo4jTemplate.toExecutableQuery(PesticideEntity.class, parameters).getResults();
    }
}
