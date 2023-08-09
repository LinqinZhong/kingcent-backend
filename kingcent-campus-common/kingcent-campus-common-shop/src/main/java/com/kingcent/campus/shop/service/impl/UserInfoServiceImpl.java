package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.UserInfoEntity;
import com.kingcent.campus.shop.mapper.UserInfoMapper;
import com.kingcent.campus.shop.service.UserInfoService;

import java.util.Map;
import java.util.Set;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoEntity> implements UserInfoService {

    @Override
    public Map<Long, UserInfoEntity> userInfoMap(Set<Long> userIds) {
        return null;
    }
}
