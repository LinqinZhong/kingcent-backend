package com.kingcent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.common.shop.entity.GroupPointPathEntity;
import com.kingcent.common.shop.mapper.GroupPointPathMapper;
import com.kingcent.service.GroupPointPathService;
import com.kingcent.common.shop.util.GroupPointUtil;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class AppGroupPointPathService extends ServiceImpl<GroupPointPathMapper, GroupPointPathEntity> implements GroupPointPathService {


    @Override
    public Result<?> getPath(){
        List<Integer> pointIds = new ArrayList<>();
        pointIds.add(1);
        pointIds.add(3);
        pointIds.add(5);
        pointIds.add(7);
        pointIds.add(8);
        pointIds.add(11);
        pointIds.add(23);
        pointIds.add(24);
        pointIds.add(25);
        pointIds.add(27);
        List<GroupPointPathEntity> paths = list(new QueryWrapper<GroupPointPathEntity>()
                .in("point1", pointIds)
                .in("point2", pointIds)
        );
        if(paths.size() == 0){
            return Result.fail("路径不存在");
        }

        //生成邻接表
        Map<Long,Map<Long, GroupPointUtil.PathInfo>> pathInfo = new HashMap<>();
        for (GroupPointPathEntity path : paths) {
            Long point1Id = path.getPoint1();
            Long point2Id = path.getPoint2();
            Map<Long, GroupPointUtil.PathInfo> p1Paths = pathInfo.computeIfAbsent(point1Id, k -> new HashMap<>());
            Map<Long, GroupPointUtil.PathInfo> p2Paths = pathInfo.computeIfAbsent(point2Id, k -> new HashMap<>());
            p1Paths.put(point2Id, new GroupPointUtil.PathInfo(path.getPassPoints(), path.getCost()));
            p2Paths.put(point1Id, new GroupPointUtil.PathInfo(GroupPointUtil.reversePath(path.getPassPoints()), path.getCost()));
        }

        pathInfo.forEach((p1, map) -> map.forEach((p2, path) -> System.out.println(p1+"->"+p2+"="+path.cost+"...."+path.crossPoints)));

        return null;
    }
}
