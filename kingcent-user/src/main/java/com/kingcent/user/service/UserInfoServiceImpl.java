package com.kingcent.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.user.entity.UserInfoEntity;
import com.kingcent.common.user.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2023/12/12 15:44
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoEntity> implements UserInfoService{

    @Override
    public Result<UserInfoEntity> get(Long userId){
        UserInfoEntity userInfo = getById(userId);
        if(userInfo != null) return Result.success(userInfo);
        return Result.fail("用户不存在");
    }

    @Override
    public Result<List<UserInfoEntity>> heads(Collection<Long> userIds) {
        return Result.success(listByIds(userIds));
    }
}
