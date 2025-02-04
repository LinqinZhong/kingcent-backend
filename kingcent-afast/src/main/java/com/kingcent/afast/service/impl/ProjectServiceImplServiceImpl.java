package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.dto.ProjectServiceDto;
import com.kingcent.afast.entity.*;
import com.kingcent.afast.mapper.ProjectServiceImplMapper;
import com.kingcent.afast.mapper.ProjectServiceMapper;
import com.kingcent.afast.service.*;
import com.kingcent.afast.utils.ProjectServiceUtil;
import com.kingcent.afast.vo.ProjectServiceVo;
import com.kingcent.common.entity.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
@Service
public class ProjectServiceImplServiceImpl extends ServiceImpl<ProjectServiceImplMapper, ProjectServiceImplEntity> implements ProjectServiceImplService {

    @Autowired
    private ProjectEntityService projectEntityService;

    @Autowired
    private ProjectServiceFuncService projectServiceFuncService;

    @Autowired
    private ProjectDaoService projectDaoService;

    @Autowired
    private ProjectServiceService projectServiceService;


    @Override
    public Result<List<ProjectServiceImplEntity>> list(Long userId, Long projectId, Long serviceId) {
        LambdaQueryWrapper<ProjectServiceImplEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectServiceImplEntity::getProjectId, projectId);
        wrapper.eq(ProjectServiceImplEntity::getServiceId,serviceId);
        return Result.success(list(wrapper));
    }

    @Override
    public Result<?> save(Long userId, Long projectId,Long serviceId, ProjectServiceImplEntity serviceImplEntity) {
        if (serviceImplEntity.getId() != null){
            //修改时不允许修改绑定的服务
            serviceImplEntity.setServiceId(null);
            serviceImplEntity.setProjectId(null);
        }else {
            serviceImplEntity.setProjectId(projectId);
            serviceImplEntity.setServiceId(serviceId);
        }
        saveOrUpdate(serviceImplEntity);
        return Result.success("保存成功");
    }

    @Override
    public Result<?> delete(Long userId, Long projectId, Long serviceImplId) {
        LambdaQueryWrapper<ProjectServiceImplEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectServiceImplEntity::getProjectId,projectId);
        wrapper.eq(ProjectServiceImplEntity::getId,serviceImplId);
        remove(wrapper);
        return Result.success("删除成功");
    }
}