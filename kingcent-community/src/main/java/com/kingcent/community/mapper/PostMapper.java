package com.kingcent.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.community.entity.PostEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author rainkyzhong
 * @date 2023/12/12 14:40
 */
@Mapper
public interface PostMapper extends BaseMapper<PostEntity> {
}
