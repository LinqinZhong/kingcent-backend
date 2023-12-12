package com.kingcent.campus.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.user.entity.UserInfoEntity;

import java.util.Collection;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/12/12 15:44
 */
public interface UserInfoService extends IService<UserInfoEntity> {
    Result<UserInfoEntity> get(Long userId);

    Result<List<UserInfoEntity>> heads(Collection<Long> userIds);
}
