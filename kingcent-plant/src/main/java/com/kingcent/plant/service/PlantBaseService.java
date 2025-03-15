package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.PlantBaseEntity;
import com.kingcent.plant.entity.ProducingEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface PlantBaseService extends IService<PlantBaseEntity> {
    Page<PlantBaseEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(PlantBaseEntity entity);
    Result<?> delete(Long id);
}
