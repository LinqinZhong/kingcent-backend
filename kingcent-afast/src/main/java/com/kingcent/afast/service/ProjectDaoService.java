package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.dto.ProjectDaoDto;
import com.kingcent.afast.dto.ProjectEntityDto;
import com.kingcent.afast.entity.ProjectDaoEntity;
import com.kingcent.afast.entity.ProjectEntityEntity;
import com.kingcent.afast.vo.ProjectDaoVo;
import com.kingcent.common.entity.result.Result;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
public interface ProjectDaoService extends IService<ProjectDaoEntity> {
    Result<Page<ProjectDaoVo>> list(Long userId, Long projectId, Long pageSize, Long pageNum);

    Result<?> save(Long userId, Long projectId, ProjectDaoDto daoDto);

    Result<?> delete(Long userId, Long projectId, Long daoId);
}
