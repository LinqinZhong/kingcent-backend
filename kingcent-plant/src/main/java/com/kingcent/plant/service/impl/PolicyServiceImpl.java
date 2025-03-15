package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.FertilizerEntity;
import com.kingcent.plant.entity.PolicyEntity;
import com.kingcent.plant.mapper.FertilizerMapper;
import com.kingcent.plant.mapper.PolicyMapper;
import com.kingcent.plant.service.FertilizerService;
import com.kingcent.plant.service.PesticideService;
import com.kingcent.plant.service.PolicyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class PolicyServiceImpl extends ServiceImpl<PolicyMapper, PolicyEntity> implements PolicyService {

    @Override
    public Page<PolicyEntity> getPage(Integer pageNum, Integer pageSize){
        Page<PolicyEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(PolicyEntity entity){
        saveOrUpdate(entity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }
}
