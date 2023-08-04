
package com.kingcent.campus.auth.servcice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.auth.entity.AdminLoginEntity;
import com.kingcent.campus.auth.entity.UserLoginEntity;
import com.kingcent.campus.auth.mapper.AdminLoginMapper;
import com.kingcent.campus.auth.mapper.UserLoginMapper;
import com.kingcent.campus.auth.servcice.AdminLoginService;
import com.kingcent.campus.auth.servcice.AdminService;
import com.kingcent.campus.auth.servcice.UserLoginService;
import org.springframework.stereotype.Service;

@Service
public class AdminLoginServiceImpl extends ServiceImpl<AdminLoginMapper, AdminLoginEntity> implements AdminLoginService {
}
