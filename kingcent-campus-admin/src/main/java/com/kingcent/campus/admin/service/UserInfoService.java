package com.kingcent.campus.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.UserInfoEntity;

import java.util.Map;
import java.util.Set;

public interface UserInfoService extends IService<UserInfoEntity> {
    Result getUser(Long id);
}
