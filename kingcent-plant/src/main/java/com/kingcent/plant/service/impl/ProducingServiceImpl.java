package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.ProducingEntity;
import com.kingcent.plant.mapper.ProducingMapper;
import com.kingcent.plant.service.ProducingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class ProducingServiceImpl extends ServiceImpl<ProducingMapper, ProducingEntity> implements ProducingService {

    @Override
    public Page<ProducingEntity> getPage(Integer pageNum, Integer pageSize){
        Page<ProducingEntity> page = page(new Page<>(pageNum, pageSize));
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(ProducingEntity entity){
        saveOrUpdate(entity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }
}
