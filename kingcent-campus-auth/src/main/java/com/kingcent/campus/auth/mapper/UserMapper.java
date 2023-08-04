package com.kingcent.campus.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.campus.auth.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}