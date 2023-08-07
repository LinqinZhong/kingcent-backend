package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.GroupPointEntity;
import com.kingcent.campus.shop.mapper.GroupPointMapper;
import com.kingcent.campus.shop.service.GroupPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class GroupPointServiceImpl extends ServiceImpl<GroupPointMapper, GroupPointEntity> implements GroupPointService {

    private static final String KEY_OF_POINT_NAMES = "point_names";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Map<Long, String> getPointNames(Set<Long> ids){
        Set<Long> newIds = new HashSet<>();
        Map<Long, String> res = new HashMap<>();
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(KEY_OF_POINT_NAMES);
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
            List<GroupPointEntity> list = list(new QueryWrapper<GroupPointEntity>()
                    .in("id", newIds)
                    .select("id, name")
            );
            for (GroupPointEntity point : list) {
                ops.put(point.getId() + "", point.getName()); //写入redis缓存
                res.put(point.getId(), point.getName()); //整合到结果集
            }
        }
        return res;
    }
}
