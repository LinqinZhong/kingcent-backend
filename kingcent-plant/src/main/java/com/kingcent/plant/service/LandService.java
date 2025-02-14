package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.LandEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface LandService extends IService<LandEntity> {
    Page<LandEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(LandEntity landEntity);

    Result<?> delete(Long landId);
}
