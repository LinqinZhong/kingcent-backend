package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.FertilizerEntity;
import com.kingcent.plant.entity.PolicyEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface PolicyService extends IService<PolicyEntity> {
    Page<PolicyEntity> getPage(Integer pageNum, Integer pageSize);

    Result<?> addOrUpdate(PolicyEntity entity);
    Result<?> delete(Long id);
}
