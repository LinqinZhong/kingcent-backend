package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.UserInfoEntity;
import com.kingcent.campus.shop.mapper.UserInfoMapper;
import com.kingcent.campus.shop.service.UserInfoService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoEntity> implements UserInfoService {

    @Override
    public Map<Long, UserInfoEntity> userInfoMap(Set<Long> userIds){
        Map<Long, UserInfoEntity> map = new HashMap<>();
        if(userIds.size() == 0) return map;
        List<UserInfoEntity> entities = list(new QueryWrapper<UserInfoEntity>().in("user_id",userIds));
        for (UserInfoEntity entity : entities) {
            map.put(entity.getUserId(), entity);
        }
        return map;
    }
}
