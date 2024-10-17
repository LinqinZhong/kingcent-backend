package com.kingcent.afast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.afast.entity.ServiceEntity;
import com.kingcent.common.entity.result.Result;

/**
 * @author rainkyzhong
 * @date 2024/10/14 23:09
 */
public interface ServiceService extends IService<ServiceEntity> {
    void create(Long userId, Long groupId, Long ecoId, String name);

    Result<Page<ServiceEntity>> list(Long userId, Long pageSize, Long pageNum, Long[] groupIds, Long[] serviceIds);
}
