package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.PlanEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface PlanService extends IService<PlanEntity> {
    Page<PlanEntity> getPage(Integer pageNum, Integer pageSize);
    Result<?> addOrUpdate(PlanEntity planEntity);

    Result<?> delete(Long planId);
}
