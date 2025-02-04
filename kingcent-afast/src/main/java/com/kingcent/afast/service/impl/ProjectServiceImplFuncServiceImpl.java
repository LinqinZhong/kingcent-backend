package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.entity.ProjectServiceFuncEntity;
import com.kingcent.afast.entity.ProjectServiceImplEntity;
import com.kingcent.afast.entity.ProjectServiceImplFuncEntity;
import com.kingcent.afast.mapper.ProjectServiceImplFuncMapper;
import com.kingcent.afast.service.ProjectServiceFuncService;
import com.kingcent.afast.service.ProjectServiceImplFuncService;
import com.kingcent.afast.service.ProjectServiceImplService;
import com.kingcent.common.entity.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2024/10/17 17:48
 */
@Service
public class ProjectServiceImplFuncServiceImpl extends ServiceImpl<ProjectServiceImplFuncMapper, ProjectServiceImplFuncEntity> implements ProjectServiceImplFuncService {


    @Autowired
    private ProjectServiceImplService projectServiceImplService;

    @Autowired
    private ProjectServiceFuncService projectServiceFuncService;


    /**
     * 获取未实现的方法
     */
    @Override
    public Result<List<ProjectServiceFuncEntity>> unimplementedFunctions(Long userId, Long projectId, Long serviceImplId){
        ProjectServiceImplEntity serviceImpl = projectServiceImplService.getById(serviceImplId);
        if (serviceImpl == null){
            return Result.fail("实现类不存在");
        }
        LambdaQueryWrapper<ProjectServiceImplFuncEntity> w1 = new LambdaQueryWrapper<>();
        w1.eq(ProjectServiceImplFuncEntity::getProjectId,projectId);
        w1.eq(ProjectServiceImplFuncEntity::getImplId,serviceImplId);
        w1.ne(ProjectServiceImplFuncEntity::getFuncId,-1);
        w1.select(ProjectServiceImplFuncEntity::getFuncId);
        List<ProjectServiceImplFuncEntity> implFunctions = list(w1);
        List<Long> ids = new ArrayList<>();
        for (ProjectServiceImplFuncEntity implFunction : implFunctions) {
            if(implFunction.getFuncId() != null){
                ids.add(implFunction.getFuncId());
            }
        }
        LambdaQueryWrapper<ProjectServiceFuncEntity> w2 = new LambdaQueryWrapper<>();
        w2.eq(ProjectServiceFuncEntity::getProjectId,projectId);
        w2.eq(ProjectServiceFuncEntity::getServiceId,serviceImpl.getServiceId());
        if(ids.size() > 0) w2.notIn(ProjectServiceFuncEntity::getId,ids);
        return Result.success(projectServiceFuncService.list(w2));
    }

    @Override
    public Result<List<ProjectServiceImplFuncEntity>> list(Long userId, Long projectId, Long serviceImplId){
        ProjectServiceImplEntity serviceImpl = projectServiceImplService.getById(serviceImplId);
        if (serviceImpl == null){
            return Result.fail("实现类不存在");
        }
        LambdaQueryWrapper<ProjectServiceImplFuncEntity> w2 = new LambdaQueryWrapper<>();
        w2.eq(ProjectServiceImplFuncEntity::getProjectId,projectId);
        w2.eq(ProjectServiceImplFuncEntity::getImplId,serviceImplId);
        List<ProjectServiceImplFuncEntity> implFunctions = list(w2);
        return Result.success(implFunctions);
    }

    @Override
    public Result<?> createImplements(Long userId, Long projectId, Long serviceImplId, List<Long> funcIds){
        ProjectServiceImplEntity serviceImpl = projectServiceImplService.getById(serviceImplId);
        if (serviceImpl == null){
            return Result.fail("实现类不存在");
        }
        LambdaQueryWrapper<ProjectServiceFuncEntity> w1 = new LambdaQueryWrapper<>();
        w1.eq(ProjectServiceFuncEntity::getProjectId,projectId);
        w1.eq(ProjectServiceFuncEntity::getServiceId,serviceImpl.getServiceId());
        if(funcIds.size() > 0) w1.in(ProjectServiceFuncEntity::getId,funcIds);
        List<ProjectServiceImplFuncEntity> implFunctions = new ArrayList<>();
        for (ProjectServiceFuncEntity func : projectServiceFuncService.list(w1)) {
            ProjectServiceImplFuncEntity implFunc = new ProjectServiceImplFuncEntity();
            implFunc.setProjectId(projectId);
            implFunc.setImplId(serviceImplId);
            implFunc.setName(func.getName());
            implFunc.setDescription(func.getDescription());
            implFunc.setParams(func.getParams());
            implFunc.setScope(0);
            implFunc.setServiceId(func.getServiceId());
            implFunc.setFuncId(func.getId());
            implFunc.setType(0);
            implFunc.setCreateTime(LocalDateTime.now());
            implFunc.setEntityId(func.getEntityId());
            implFunc.setReturnParam(func.getReturnParam());
            implFunctions.add(implFunc);
        }
        if(implFunctions.size() > 0){
            saveBatch(implFunctions);
        }
        return Result.success();
    }
}
