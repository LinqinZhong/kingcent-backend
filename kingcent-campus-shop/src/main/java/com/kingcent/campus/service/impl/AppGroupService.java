package com.kingcent.campus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.common.entity.vo.VoList;
import com.kingcent.campus.shop.entity.GroupEntity;
import com.kingcent.campus.shop.entity.vo.group.GroupLocationVo;
import com.kingcent.campus.shop.mapper.GroupMapper;
import com.kingcent.campus.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.BoundGeoOperations;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Slf4j
public class AppGroupService extends ServiceImpl<GroupMapper, GroupEntity> implements GroupService {
    private static final String KEY_OF_GROUP_POINTS = "group_points";
    private static final String KEY_OF_GROUP_NAMES = "group_names";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public Map<Long, String> getGroupNames(Set<Long> ids){
        Set<Long> newIds = new HashSet<>();
        Map<Long, String> res = new HashMap<>();
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(KEY_OF_GROUP_NAMES);
        for (Long id : ids) {
            String name = (String) ops.get(id+"");
            if(name != null){
                //缓存中存在，不从数据库中读取
                res.put(id, name);
            }else{
                newIds.add(id);
            }
        }
        if(newIds.size() > 0) {
            //从数据库中读取
            List<GroupEntity> list = list(new QueryWrapper<GroupEntity>()
                    .in("id", newIds)
                    .select("id, name")
            );
            for (GroupEntity group : list) {
                ops.put(group.getId() + "", group.getName()); //写入redis缓存
                res.put(group.getId(), group.getName()); //整合到结果集
            }
        }
        return res;
    }

    /**
     * 当redis没有数据的时候，初始化数据
     */
    private void initIfNoneData(){
        if(Boolean.FALSE.equals(redisTemplate.hasKey(KEY_OF_GROUP_POINTS))){
            initPointLocations();
        }
    }

//    @Override
//    public Result<List<GroupLocationVo>> fetchNearbyGroups(Double longitude, Double latitude){
//
//        initIfNoneData();
//
//        BoundGeoOperations<String, String> ops = redisTemplate.boundGeoOps(KEY_OF_GROUP_POINTS);
//
//        List<GroupLocationVo> list = new ArrayList<>();
//
//        //获取点
//        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
//                .GeoRadiusCommandArgs
//                .newGeoRadiusArgs()
//                .includeDistance()
//                .includeCoordinates()
//                .sortAscending()
//                .limit(30);
//        GeoResults<RedisGeoCommands.GeoLocation<String>> radius =
//                ops.radius(
//                        new Circle(
//                                new Point(longitude, latitude),
//                                new Distance(2000D, RedisGeoCommands.DistanceUnit.METERS)
//                        ),
//                        args
//                );
//        if(radius == null || radius.getContent().size() == 0){
//            return Result.success(list);
//        }
//        //提取内容
//        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : radius) {
//            try {
//                System.out.println(result);
//                String[] info = result.getContent().getName().split(",");
//                GroupLocationVo vo = new GroupLocationVo();
//                vo.setGroupId(Long.valueOf(info[0]));
//                vo.setName(info[1]);
//                vo.setDistance(result.getDistance().getValue());
//                vo.setLongitude(result.getContent().getPoint().getX());
//                vo.setLatitude(result.getContent().getPoint().getY());
//                list.add(vo);
//            }catch (Exception e){
//                log.warn("组点坐标出现错误内容");
//                e.printStackTrace();
//            }
//        }
//        //按距离排序
//        list.sort((o1, o2) -> (int) (o1.getDistance() - o2.getDistance()));
//
//        return Result.success(list);
//    }

//    @Override
//    public Result<GroupLocationVo> fetchNearestGroup(Double longitude, Double latitude){
//
//        initIfNoneData();
//
//        BoundGeoOperations<String, String> ops = redisTemplate.boundGeoOps(KEY_OF_GROUP_POINTS);
//
//        //获取点
//        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands
//                .GeoRadiusCommandArgs
//                .newGeoRadiusArgs()
//                .includeDistance()
//                .includeCoordinates()
//                .sortAscending()
//                .limit(1);
//        GeoResults<RedisGeoCommands.GeoLocation<String>> radius =
//                ops.radius(
//                        new Circle(
//                                new Point(longitude, latitude),
//                                new Distance(2000D, RedisGeoCommands.DistanceUnit.METERS)
//                        ),
//                        args
//                );
//        if(radius == null || radius.getContent().size() == 0){
//            return Result.fail("附近没有楼栋");
//        }
//        String[] info = radius.getContent().get(0).getContent().getName().split(",");
//        GroupLocationVo vo = new GroupLocationVo();
//        vo.setGroupId(Long.valueOf(info[0]));
//        vo.setName(info[1]);
//
//        return Result.success(vo);
//    }

    @Override
    public Result<List<GroupLocationVo>> ofSite(Long siteId) {
        QueryWrapper<GroupEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("site_id", siteId);
        List<GroupLocationVo> groups = new ArrayList<>();
        List<GroupEntity> res = list(wrapper);
        for (GroupEntity record : res) {
            groups.add(new GroupLocationVo(
                    record.getId(),
                    record.getName(),
                    record.getLongitude(),
                    record.getLatitude()
            ));
        }
        return Result.success(groups);
    }


    /**
     * 初始化redis中的group点信息
     */
    @Override
    public void initPointLocations(){
        log.info("初始化楼栋坐标信息");
        List<GroupEntity> list = list();
        log.info("点数："+list.size());
        BoundGeoOperations<String, String> ops = redisTemplate.boundGeoOps(KEY_OF_GROUP_POINTS);
        for (GroupEntity group : list) {
            Point point = new Point(group.getLongitude(), group.getLatitude());
            ops.add(point, group.getId()+","+group.getName());
        }
        log.info("楼栋坐标初始化成功");
    }
}
