package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.entity.LandPhEntity;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface LandPhService extends IService<LandPhEntity> {
    List<LandPhEntity> byHours(Long landId);
    List<LandPhEntity> byDays(Long landId);
    LandPhEntity getNewData(Long landId);
}
