package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.entity.ProjectDaoFuncEntity;
import com.kingcent.afast.entity.ProjectServiceFuncEntity;
import com.kingcent.common.entity.result.Result;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
public interface ProjectServiceFuncService extends IService<ProjectServiceFuncEntity> {
    Result<Page<ProjectServiceFuncEntity>> list(Long userId, Long projectId, Long serviceId,  Long pageNum, Long pageSize);

    Result<?> save(Long userId, Long projectId, Long serviceId, ProjectServiceFuncEntity func);

    Result<?> delete(Long userId, Long projectId, Long serviceId, Long funcId);
}
