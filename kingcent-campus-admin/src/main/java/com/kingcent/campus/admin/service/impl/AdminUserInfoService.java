package com.kingcent.campus.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.admin.service.UserInfoService;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.UserInfoEntity;
import com.kingcent.campus.shop.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * @author zzy
 */
@Service
public class AdminUserInfoService extends ServiceImpl<UserInfoMapper, UserInfoEntity> implements UserInfoService {

    @Override
    public Result getUser(Long id) {
        UserInfoEntity userInfo = getById(id);
        if(userInfo != null) {
            return Result.success(userInfo);
        }else {
            return Result.fail("找不到该用户，请确认输入id");
        }
    }
}
