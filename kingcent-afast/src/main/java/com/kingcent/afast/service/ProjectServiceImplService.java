package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.dto.ProjectServiceDto;
import com.kingcent.afast.entity.ProjectServiceEntity;
import com.kingcent.afast.entity.ProjectServiceImplEntity;
import com.kingcent.afast.vo.ProjectServiceVo;
import com.kingcent.common.entity.result.Result;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
public interface ProjectServiceImplService extends IService<ProjectServiceImplEntity> {
    Result<List<ProjectServiceImplEntity>> list(Long userId, Long projectId, Long serviceId);

    Result<?> save(Long userId, Long projectId, ProjectServiceImplEntity serviceImplEntity);

    Result<?> delete(Long userId, Long projectId, Long entityId);
}
