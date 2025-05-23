package com.kingcent.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.common.user.entity.UserLoginEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginMapper extends BaseMapper<UserLoginEntity> {
}
