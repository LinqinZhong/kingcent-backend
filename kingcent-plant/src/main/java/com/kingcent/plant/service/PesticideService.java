package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.DamageEntity;
import com.kingcent.plant.entity.PesticideEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface PesticideService extends IService<PesticideEntity> {
    Page<PesticideEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(PesticideEntity entity);
    Result<?> delete(Long id);
}
