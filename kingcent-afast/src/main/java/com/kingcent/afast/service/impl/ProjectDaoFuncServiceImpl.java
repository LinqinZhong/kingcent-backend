package com.kingcent.afast.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.entity.EcologyEntity;
import com.kingcent.afast.entity.ProjectDaoFuncEntity;
import com.kingcent.afast.entity.ProjectEntityEntity;
import com.kingcent.afast.mapper.ProjectDaoFuncMapper;
import com.kingcent.afast.service.ProjectDaoFuncService;
import com.kingcent.common.entity.result.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2024/10/17 17:48
 */
@Service
public class ProjectDaoFuncServiceImpl extends ServiceImpl<ProjectDaoFuncMapper, ProjectDaoFuncEntity> implements ProjectDaoFuncService {
    @Override
    public Result<Page<ProjectDaoFuncEntity>> list(Long userId, Long projectId, Long daoId, Long pageSize, Long pageNum) {
        LambdaQueryWrapper<ProjectDaoFuncEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectDaoFuncEntity::getProjectId, projectId);
        wrapper.eq(ProjectDaoFuncEntity::getDaoId, daoId);
        return Result.success(page(new Page<>(pageNum,pageSize),wrapper));
    }

    @Override
    public Result<?> save(Long userId, Long projectId,Long daoId, ProjectDaoFuncEntity func) {
        func.setProjectId(projectId);
        func.setDaoId(daoId);
        func.setCreateTime(LocalDateTime.now());

        //处理传过来的params字段
        String params = func.getParams();
        if(params == null) params = "[]";
        else{
            try{
                List<Map<String,Object>> res = new ArrayList<>();
                List<JSONObject> list = JSON.parseArray(params, JSONObject.class);
                for (JSONObject jsonObject : list) {
                    if (jsonObject != null){
                        String name = jsonObject.getString("name");
                        String type = jsonObject.getString("type");
                        String description = (String) jsonObject.getOrDefault("description","");
                        res.add(
                                Map.of(
                                        "name",name,
                                        "type",type,
                                        "description",description
                                )
                        );
                    }
                }
                params = JSON.toJSONString(res);
            }catch (Exception e){
                return Result.fail("参数错误");
            }
        }
        func.setParams(params);


        //检测是否存在重复的方法
        Long id = baseMapper.signExist(
                projectId,
                daoId,
                func.getParams(),
                func.getReturnParam(),
                func.getName()
        );
        if(id != null && !id.equals(func.getId())){
            return Result.fail("已存在同样的方法");
        }

        saveOrUpdate(func);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long userId, Long projectId, Long daoId, Long funcId) {
        LambdaQueryWrapper<ProjectDaoFuncEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectDaoFuncEntity::getProjectId,projectId);
        wrapper.eq(ProjectDaoFuncEntity::getDaoId,daoId);
        wrapper.eq(ProjectDaoFuncEntity::getId,funcId);
        remove(wrapper);
        return Result.success("删除成功");
    }
}
