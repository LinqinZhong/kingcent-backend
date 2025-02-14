package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.VarietyEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface VarietyService extends IService<VarietyEntity> {
    Page<VarietyEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(VarietyEntity varietyEntity);
    Result<?> delete(Long id);
}
