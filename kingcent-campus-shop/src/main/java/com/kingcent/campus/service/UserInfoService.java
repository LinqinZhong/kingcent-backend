package com.kingcent.campus.service;


import com.kingcent.campus.user.entity.UserInfoEntity;

import java.util.Map;
import java.util.Set;

public interface UserInfoService {
    Map<Long, UserInfoEntity> userInfoMap(Set<Long> userIds);

    UserInfoEntity get(Long userId);
}
