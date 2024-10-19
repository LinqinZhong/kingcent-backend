package com.kingcent.auth.servcice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.auth.servcice.UserService;
import com.kingcent.auth.mapper.UserMapper;
import com.kingcent.auth.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

}
