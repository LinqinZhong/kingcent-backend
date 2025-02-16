package com.kingcent.auth.servcice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.auth.servcice.UserService;
import com.kingcent.auth.mapper.UserMapper;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Override
    public Result<UserEntity> create(UserEntity userEntity) {
        String username = userEntity.getUsername();
        if(username == null) return Result.fail("用户名不能为空");
        String password = userEntity.getPassword();
        if(password == null) return Result.fail("密码不能为空");
        String passwordSalt = userEntity.getPasswordSalt();
        if(passwordSalt == null) return Result.fail("密码盐不能为空");
        userEntity.setUsername(username.trim());
        userEntity.setPassword(password.trim());
        userEntity.setPasswordSalt(passwordSalt.trim());
        long count = count(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username)
        );
        userEntity.setCreateTime(LocalDateTime.now());
        if(count > 0) return Result.fail("用户名已存在");
        save(userEntity);
        return Result.success(userEntity);
    }
}
