package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.entity.ProjectMvnDepEntity;
import com.kingcent.afast.mapper.ProjectMvnDepMapper;
import com.kingcent.afast.service.ProjectMvnDepService;
import com.kingcent.common.result.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/20 0:07
 */
@Service
public class ProjectMvnDepServiceImpl extends ServiceImpl<ProjectMvnDepMapper, ProjectMvnDepEntity> implements ProjectMvnDepService {
    @Override
    public List<ProjectMvnDepEntity> getProjectDependencies(Long projectId, boolean targetOnUsing) {
        LambdaQueryWrapper<ProjectMvnDepEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMvnDepEntity::getProjectId,projectId);
        if(targetOnUsing){
            wrapper.eq(ProjectMvnDepEntity::getStatus,1);
        }
        return list(wrapper);
    }

    @Override
    public Result<Page<ProjectMvnDepEntity>> list(Long userId, Long projectId, Long pageNum, Long pageSize, Integer status){
        LambdaQueryWrapper<ProjectMvnDepEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectMvnDepEntity::getProjectId, projectId);
        if(status != null){
            wrapper.eq(ProjectMvnDepEntity::getStatus,status);
        }
        return Result.success(page(new Page<>(pageNum,pageSize),wrapper));
    }
}
