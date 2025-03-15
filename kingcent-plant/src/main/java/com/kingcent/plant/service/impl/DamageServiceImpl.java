package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.VarietyEntity;
import com.kingcent.plant.mapper.DamageMapper;
import com.kingcent.plant.mapper.VarietyMapper;
import com.kingcent.plant.service.DamageService;
import com.kingcent.plant.service.VarietyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class DamageServiceImpl extends ServiceImpl<DamageMapper, DamageEntity> implements DamageService {

    @Override
    public Page<DamageEntity> getPage(Integer pageNum, Integer pageSize){
        Page<DamageEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(DamageEntity damageEntity){
        saveOrUpdate(damageEntity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }
}
