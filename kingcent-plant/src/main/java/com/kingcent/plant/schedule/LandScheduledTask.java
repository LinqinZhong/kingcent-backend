package com.kingcent.plant.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.entity.LandPhEntity;
import com.kingcent.plant.entity.LandWaterEntity;
import com.kingcent.plant.service.LandPhService;
import com.kingcent.plant.service.LandService;
import com.kingcent.plant.service.LandWaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class LandScheduledTask {

    @Autowired
    private LandService landService;

    @Autowired
    private LandPhService landPhService;


    @Autowired
    private LandWaterService landWaterService;

    public LandScheduledTask(LandPhService landPhService) {
        this.landPhService = landPhService;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60) // 每小时执行一次
    public void insertDataHourly() {
        // 统计PH值
        List<LandEntity> list = landService.list(new LambdaQueryWrapper<LandEntity>().select(LandEntity::getId));
        for (LandEntity landEntity : list) {
            LandPhEntity newData = landPhService.getNewData(landEntity.getId());
            if (newData != null) {
                // 设置创建时间为当前时间
                newData.setCreateTime(LocalDateTime.now());
                landPhService.save(newData);
            }
            LandWaterEntity waterEntity = landWaterService.getNewData(landEntity.getId());
            if(waterEntity != null){
                // 设置创建时间为当前时间
                waterEntity.setCreateTime(LocalDateTime.now());
                landWaterService.save(waterEntity);
            }
        }
    }
}    