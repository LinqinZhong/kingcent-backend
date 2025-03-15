package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.FertilizerEntity;
import com.kingcent.plant.mapper.DamageMapper;
import com.kingcent.plant.mapper.FertilizerMapper;
import com.kingcent.plant.service.DamageService;
import com.kingcent.plant.service.FertilizerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class FertilizerServiceImpl extends ServiceImpl<FertilizerMapper, FertilizerEntity> implements FertilizerService {

    @Override
    public Page<FertilizerEntity> getPage(Integer pageNum, Integer pageSize){
        Page<FertilizerEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(FertilizerEntity entity){
        saveOrUpdate(entity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }
}
