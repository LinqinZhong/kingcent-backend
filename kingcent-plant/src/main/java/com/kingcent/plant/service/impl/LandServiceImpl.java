package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.mapper.LandMapper;
import com.kingcent.plant.service.LandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class LandServiceImpl extends ServiceImpl<LandMapper, LandEntity> implements LandService {

    @Override
    public Page<LandEntity> getPage(Integer pageNum, Integer pageSize){
        Page<LandEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(LandEntity landEntity){
        boolean isUpdate = landEntity.getId() != null;
        if(!isUpdate){
            landEntity.setStatus(0);
            landEntity.setCreateTime(LocalDateTime.now());
        }
        landEntity.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(landEntity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long landId) {
        removeById(landId);
        return Result.success();
    }
}
