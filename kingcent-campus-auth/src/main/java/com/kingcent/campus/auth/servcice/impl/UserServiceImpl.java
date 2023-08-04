package com.kingcent.campus.auth.servcice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.auth.mapper.UserMapper;
import com.kingcent.campus.auth.servcice.UserService;
import com.kingcent.campus.auth.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {
}
