package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.FertilizerEntity;
import com.kingcent.plant.entity.PesticideEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface FertilizerService extends IService<FertilizerEntity> {
    Page<FertilizerEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(FertilizerEntity entity);
    Result<?> delete(Long id);
}
