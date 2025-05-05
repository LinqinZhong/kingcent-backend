package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.PolicyEntity;
import com.kingcent.plant.entity.SaplingEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface SaplingService extends IService<SaplingEntity> {
    Page<SaplingEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(Long userId, SaplingEntity entity);
    Result<?> delete(Long id);
}
