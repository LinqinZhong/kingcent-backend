package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.PlanEntity;
import com.kingcent.plant.entity.VarietyEntity;
import com.kingcent.plant.mapper.PlanMapper;
import com.kingcent.plant.mapper.VarietyMapper;
import com.kingcent.plant.repository.VarietyRepository;
import com.kingcent.plant.service.PlanService;
import com.kingcent.plant.service.VarietyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class VarietyServiceImpl extends ServiceImpl<VarietyMapper, VarietyEntity> implements VarietyService {


    @Autowired
    private VarietyRepository varietyRepository;

    @Override
    public Page<VarietyEntity> getPage(Integer pageNum, Integer pageSize){
        Page<VarietyEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(VarietyEntity varietyEntity){
        saveOrUpdate(varietyEntity);
        varietyRepository.save(varietyEntity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }
}
