package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.entity.ProjectEntity;
import com.kingcent.common.entity.result.Result;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
public interface ProjectService extends IService<ProjectEntity> {

    void create(Long userId, Long groupId, String name);

    Result<Page<ProjectEntity>> list(Long userId,Long groupId, Long pageSize, Long pageNum);
}
