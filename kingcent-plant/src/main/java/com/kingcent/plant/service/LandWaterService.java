package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.LandWaterEntity;
import com.kingcent.plant.entity.LandWaterEntity;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface LandWaterService extends IService<LandWaterEntity> {
    List<LandWaterEntity> byHours(Long landId);
    List<LandWaterEntity> byDays();

    LandWaterEntity getNewData(Long landId);
}
