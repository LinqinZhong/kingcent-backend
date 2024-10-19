package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.entity.ProjectMvnDepEntity;
import com.kingcent.common.entity.result.Result;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/20 0:06
 */
public interface ProjectMvnDepService extends IService<ProjectMvnDepEntity> {
    List<ProjectMvnDepEntity> getProjectDependencies(Long projectId,boolean targetOnUsing);

    Result<Page<ProjectMvnDepEntity>> list(Long userId, Long projectId, Long pageNum, Long pageSize, Integer status);
}
