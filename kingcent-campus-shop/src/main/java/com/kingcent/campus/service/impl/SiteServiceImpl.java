package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.SiteEntity;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.entity.vo.site.SiteLocationVo;
import com.kingcent.campus.mapper.SiteMapper;
import com.kingcent.campus.service.SiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SiteServiceImpl extends ServiceImpl<SiteMapper, SiteEntity> implements SiteService {

    private static final String KEY_OF_GROUP_SITES = "group_sites";
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public Result<List<SiteLocationVo>> fetchNearestSite(Double longitude, Double latitude){
        BoundGeoOperations<String, String> ops = redisTemplate.boundGeoOps(KEY_OF_GROUP_SITES);
        //获取点
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
                .GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();
        GeoResults<RedisGeoCommands.GeoLocation<String>> radius =
                ops.radius(
                        new Circle(
                                new Point(longitude, latitude),
                                new Distance(5000D, RedisGeoCommands.DistanceUnit.METERS)
                        ),
                        args
                );
        List<SiteLocationVo> list = new ArrayList<>();
        if(radius == null || radius.getContent().size() == 0){
            return Result.success(list);
        }
        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : radius.getContent()) {
            String[] info = result.getContent().getName().split(",");
            if(info.length != 2){
                continue;
            }
            SiteLocationVo vo = new SiteLocationVo();
            vo.setSiteId(Long.valueOf(info[0]));
            vo.setName(info[1]);
            list.add(vo);
        }
        return Result.success(list);
    }

    /**
     * 初始化redis中的site点信息
     */
    @Override
    public void initSiteLocations(){
        log.info("初始化配送域坐标信息");
        List<SiteEntity> list = list();
        log.info("点数："+list.size());
        BoundGeoOperations<String, String> ops = redisTemplate.boundGeoOps(KEY_OF_GROUP_SITES);
        for (SiteEntity site : list) {
            Point point = new Point(site.getLongitude(), site.getLatitude());
            ops.add(point, site.getId()+","+site.getName());
        }
        log.info("配送域坐标初始化成功");
    }
}
