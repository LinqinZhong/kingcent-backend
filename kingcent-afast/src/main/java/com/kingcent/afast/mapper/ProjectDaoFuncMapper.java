package com.kingcent.afast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.afast.entity.ProjectDaoEntity;
import com.kingcent.afast.entity.ProjectDaoFuncEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author rainkyzhong
 * @date 2024/10/16 0:13
 */
@Mapper
public interface ProjectDaoFuncMapper extends BaseMapper<ProjectDaoFuncEntity> {

    @Select("select id from kc_afast_project_dao_func " +
            "where project_id = #{projectId} " +
            "and dao_id = #{daoId} " +
            "and params = #{params} " +
            "and return_param = #{returnParam} " +
            "and name = #{name} " +
            "limit 1"
    )
    Long signExist(
            @Param("projectId") Long projectId,
            @Param("daoId") Long daoId,
            @Param("params") String params,
            @Param("returnParam") String returnParam,
            @Param("name") String name
    );
}
