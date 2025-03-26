package com.kingcent.plant.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.plant.entity.LandPhEntity;
import com.kingcent.plant.mapper.LandPhMapper;
import com.kingcent.plant.service.LandPhService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Service
public class LandPhServiceImpl extends ServiceImpl<LandPhMapper, LandPhEntity> implements LandPhService {

    @Override
    public List<LandPhEntity> byHours(Long landId) {
        QueryWrapper<LandPhEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("EXTRACT(HOUR FROM create_time) as hour", "AVG(value) as value")
                .groupBy("hour").eq("land_id",landId).ge("create_time", LocalDateTime.now().plusHours(-24));
        return this.list(queryWrapper);
    }


    @Override
    public List<LandPhEntity> byDays(Long landId) {
        QueryWrapper<LandPhEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DATE(create_time) as x", "AVG(value) as value")
                .groupBy("DATE(create_time)").eq("land_id", landId)
                .ge("create_time", LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.UTC).getEpochSecond());
        return this.list(queryWrapper);
    }

    @Override
    public LandPhEntity getNewData(Long landId) {
        LandPhEntity entity = new LandPhEntity();
        entity.setLandId(landId);
        Random random = new Random();
        double randomValue = 7 + (random.nextDouble() * 2);
        entity.setValue(randomValue);
        return entity;
    }

}