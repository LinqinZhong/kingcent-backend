package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.PolicyEntity;
import com.kingcent.plant.entity.ProducingEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface ProducingService extends IService<ProducingEntity> {
    Page<ProducingEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(ProducingEntity entity);
    Result<?> delete(Long id);
}
