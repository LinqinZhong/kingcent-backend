package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.entity.ServiceEntity;
import com.kingcent.afast.mapper.ServiceMapper;
import com.kingcent.afast.service.ServiceService;
import com.kingcent.common.result.Result;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2024/10/14 23:10
 */
@Service
public class ServiceServiceImpl extends ServiceImpl<ServiceMapper, ServiceEntity> implements ServiceService {
    @Override
    public void create(Long userId, Long groupId, Long ecoId, String name) {
        ServiceEntity service = new ServiceEntity();
        service.setGroupId(groupId);
        service.setName(name);
        save(service);
    }

    @Override
    public Result<Page<ServiceEntity>> list(Long userId, Long pageSize, Long pageNum, Long[] groupIds, Long[] serviceIds) {
        LambdaQueryWrapper<ServiceEntity> wrapper = new LambdaQueryWrapper<>();
        return Result.success("",page(new Page<>(pageNum,pageSize),wrapper));
    }
}
