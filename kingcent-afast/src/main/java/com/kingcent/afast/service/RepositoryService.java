package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.dto.RepositoryDto;
import com.kingcent.afast.entity.RepositoryEntity;
import com.kingcent.afast.vo.RepositoryBranchVo;
import com.kingcent.common.result.Result;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/16 1:58
 */
public interface RepositoryService extends IService<RepositoryEntity> {
    void create(RepositoryDto repository);

    Result<Page<RepositoryEntity>> list(Long userId,Long groupId, Long pageSize, Long pageNum);

    Result<List<RepositoryBranchVo>> branches(Long userId, Long repoId);
}
