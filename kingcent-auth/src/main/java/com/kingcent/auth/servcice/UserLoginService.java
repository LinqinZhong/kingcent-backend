package com.kingcent.auth.servcice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.user.entity.UserLoginEntity;

public interface UserLoginService extends IService<UserLoginEntity> {
    Long check(String token);
}
