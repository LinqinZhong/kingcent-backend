package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.entity.EcologyEntity;
import com.kingcent.afast.mapper.EcologyMapper;
import com.kingcent.afast.service.EcologyService;
import com.kingcent.common.entity.result.Result;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:51
 */
@Service
public class EcologyServiceImpl extends ServiceImpl<EcologyMapper,EcologyEntity> implements EcologyService {


    @Override
    public void create(Long userId, Long groupId, String name) {
        EcologyEntity ecology = new EcologyEntity();
        ecology.setGroupId(groupId);
        ecology.setName(name);
        save(ecology);
    }

    @Override
    public Result<Page<EcologyEntity>> list(Long userId, Long pageSize, Long pageNum, Long[] groupIds) {
        LambdaQueryWrapper<EcologyEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EcologyEntity::getGroupId,1L);
        return Result.success("",page(new Page<>(pageNum,pageSize),wrapper));
    }
}
