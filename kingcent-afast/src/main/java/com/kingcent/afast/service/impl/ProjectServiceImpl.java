package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.entity.ProjectEntity;
import com.kingcent.afast.entity.ServerEntity;
import com.kingcent.afast.mapper.ProjectMapper;
import com.kingcent.afast.mapper.ServerMapper;
import com.kingcent.afast.service.ProjectService;
import com.kingcent.afast.service.ServerService;
import com.kingcent.common.entity.result.Result;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> implements ProjectService {
    @Override
    public void create(Long userId, Long groupId, String name) {
        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        save(project);
    }

    @Override
    public Result<Page<ProjectEntity>> list(Long userId,Long groupId, Long pageSize, Long pageNum) {
        LambdaQueryWrapper<ProjectEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectEntity::getGroupId, groupId);
        return Result.success("",page(new Page<>(pageNum,pageSize),wrapper));
    }
}
