package com.kingcent.plant.service;

import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserEntity;

import java.util.Collection;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/15 22:05
 */

public interface UserService {
    Result<UserEntity> create(UserEntity userEntity) throws KingcentSystemException;

    Result<List<UserEntity>> getUserInfoByIds(Collection<Long> ids) throws KingcentSystemException;
}
