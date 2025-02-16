package com.kingcent.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.common.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}