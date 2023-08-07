package com.kingcent.campus.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.UserInfoEntity;

import java.util.Map;
import java.util.Set;

public interface UserInfoService extends IService<UserInfoEntity> {
    Map<Long, UserInfoEntity> userInfoMap(Set<Long> userIds);
}
