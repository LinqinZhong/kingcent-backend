package com.kingcent.campus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kingcent.campus.shop.entity.UserInfoEntity;
import com.kingcent.campus.shop.service.impl.UserInfoServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
@Service
public class UserInfoService extends UserInfoServiceImpl {

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
