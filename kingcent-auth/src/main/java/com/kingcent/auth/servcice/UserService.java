package com.kingcent.auth.servcice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserEntity;

public interface UserService extends IService<UserEntity> {
    Result<UserEntity> create(UserEntity userEntity);
}