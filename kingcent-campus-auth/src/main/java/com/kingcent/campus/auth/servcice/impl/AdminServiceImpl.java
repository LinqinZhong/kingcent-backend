package com.kingcent.campus.auth.servcice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.auth.entity.AdminEntity;
import com.kingcent.campus.auth.mapper.AdminMapper;
import com.kingcent.campus.auth.servcice.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminEntity> implements AdminService {
}
