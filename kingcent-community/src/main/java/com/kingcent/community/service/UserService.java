package com.kingcent.community.service;


import com.kingcent.common.user.entity.UserInfoEntity;

import java.util.Map;
import java.util.Set;

public interface UserService {
    Map<Long, UserInfoEntity> userInfoMap(Set<Long> userIds);
}
