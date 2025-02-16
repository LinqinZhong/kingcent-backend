package com.kingcent.plant.service;

import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/15 22:05
 */

public interface UserService {
    Result<UserEntity> create(UserEntity userEntity);
}
