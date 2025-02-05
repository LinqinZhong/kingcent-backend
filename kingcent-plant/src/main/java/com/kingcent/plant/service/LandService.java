package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.plant.entity.LandEntity;

import java.math.BigDecimal;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface LandService extends IService<LandEntity> {
    Page<LandEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> add(LandEntity landEntity);
}
