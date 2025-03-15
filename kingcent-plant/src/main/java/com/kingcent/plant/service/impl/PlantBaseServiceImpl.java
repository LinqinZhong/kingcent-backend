package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.PlantBaseEntity;
import com.kingcent.plant.mapper.PlantBaseMapper;
import com.kingcent.plant.service.PlantBaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class PlantBaseServiceImpl extends ServiceImpl<PlantBaseMapper, PlantBaseEntity> implements PlantBaseService {

    @Override
    public Page<PlantBaseEntity> getPage(Integer pageNum, Integer pageSize){
        Page<PlantBaseEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(PlantBaseEntity entity){
        saveOrUpdate(entity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }
}
