package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.entity.ServerEntity;
import com.kingcent.common.result.Result;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
public interface ServerService extends IService<ServerEntity> {

    void create(Long userId, Long groupId, String name);

    Result<Page<ServerEntity>> list(Long userId, Long pageSize, Long pageNum, Long[] groupIds);
}
