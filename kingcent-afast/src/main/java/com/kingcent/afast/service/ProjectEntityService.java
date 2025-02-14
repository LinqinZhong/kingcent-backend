package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.dto.ProjectEntityDto;
import com.kingcent.afast.entity.ProjectEntity;
import com.kingcent.afast.entity.ProjectEntityEntity;
import com.kingcent.common.result.Result;

import java.util.Map;
import java.util.Set;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
public interface ProjectEntityService extends IService<ProjectEntityEntity> {
    Result<Page<ProjectEntityEntity>> list(Long userId,Long projectId, Long pageSize, Long pageNum);

    Result<?> save(Long userId,Long projectId,ProjectEntityDto entity);

    Result<?> delete(Long userId, Long projectId, Long entityId);

    Result<String> toSql(Long userId, Long projectId, Long entityId);

    ProjectEntityEntity get(Long userId, Long projectId, Long entityId);

    Map<Long, String> getNameMap(Set<Long> entityIds);

    Result<String> toJava(Long userId, Long projectId, Long entityId);
}
