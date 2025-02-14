package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.dto.ProjectDaoDto;
import com.kingcent.afast.dto.ProjectServiceDto;
import com.kingcent.afast.entity.ProjectDaoEntity;
import com.kingcent.afast.entity.ProjectServiceEntity;
import com.kingcent.afast.vo.ProjectDaoVo;
import com.kingcent.afast.vo.ProjectServiceVo;
import com.kingcent.common.result.Result;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
public interface ProjectServiceService extends IService<ProjectServiceEntity> {
    Result<Page<ProjectServiceVo>> list(Long userId, Long projectId, Long pageSize, Long pageNum);

    Result<?> save(Long userId, Long projectId, ProjectServiceDto service);

    Result<?> delete(Long userId, Long projectId, Long entityId);

    Result<?> generateJava(Long userId, Long projectId, Long serviceId);
}
