package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.entity.ProjectServiceFuncEntity;
import com.kingcent.afast.entity.ProjectServiceImplFuncEntity;
import com.kingcent.common.result.Result;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
public interface ProjectServiceImplFuncService extends IService<ProjectServiceImplFuncEntity> {
    Result<List<ProjectServiceFuncEntity>> unimplementedFunctions(Long userId, Long projectId, Long serviceImplId);

    Result<List<ProjectServiceImplFuncEntity>> list(Long userId, Long projectId, Long serviceImplId);

    Result<?> createImplements(Long userId, Long projectId, Long serviceImplId, List<Long> funcIds);
}
