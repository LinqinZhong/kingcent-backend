package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.plant.entity.LandWaterEntity;
import com.kingcent.plant.mapper.LandWaterMapper;
import com.kingcent.plant.service.LandWaterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class LandWaterServiceImpl extends ServiceImpl<LandWaterMapper, LandWaterEntity> implements LandWaterService {

    @Override
    public List<LandWaterEntity> byHours(Long landId) {
        QueryWrapper<LandWaterEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("EXTRACT(HOUR FROM create_time) as hour", "AVG(value) as value")
                .groupBy("hour").eq("land_id",landId).ge("create_time", LocalDateTime.now().plusHours(-24));
        return this.list(queryWrapper);
    }

    @Override
    public List<LandWaterEntity> byDays() {
        QueryWrapper<LandWaterEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DATE(create_time) as create_time", "AVG(value) as value")
                .groupBy("DATE(create_time)");
        return this.list(queryWrapper);
    }

    @Override
    public LandWaterEntity getNewData(Long landId) {
        LandWaterEntity entity = new LandWaterEntity();
        entity.setLandId(landId);
        Random random = new Random();
        double randomValue = 50 + (random.nextDouble() * 40);
        entity.setValue(randomValue);
        return entity;
    }
}