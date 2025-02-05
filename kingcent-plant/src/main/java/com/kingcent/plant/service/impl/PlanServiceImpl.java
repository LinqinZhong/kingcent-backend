package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.entity.result.Result;
import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.entity.PlanEntity;
import com.kingcent.plant.mapper.LandMapper;
import com.kingcent.plant.mapper.PlanMapper;
import com.kingcent.plant.service.LandService;
import com.kingcent.plant.service.PlanService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, PlanEntity> implements PlanService {

    @Override
    public Page<PlanEntity> getPage(Integer pageNum, Integer pageSize){
        Page<PlanEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    public Result<?> add(PlanEntity planEntity){
        planEntity.setId(null);
        planEntity.setStatus(0);
        planEntity.setCreateTime(LocalDateTime.now());
        planEntity.setUpdateTime(LocalDateTime.now());
        save(planEntity);
        return Result.success();
    }
}
