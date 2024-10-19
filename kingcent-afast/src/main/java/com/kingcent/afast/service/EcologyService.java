package com.kingcent.afast.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.entity.EcologyEntity;
import com.kingcent.common.entity.result.Result;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:50
 */
public interface EcologyService extends IService<EcologyEntity> {
    void create(Long userId, Long groupId, String name);

    Result<Page<EcologyEntity>> list(Long userId, Long pageSize, Long pageNum, Long[] groupIds);
}
