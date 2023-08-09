package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.shop.entity.GroupPointEntity;
import com.kingcent.campus.shop.mapper.GroupPointMapper;
import com.kingcent.campus.shop.service.GroupPointService;

import java.util.Map;
import java.util.Set;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
public class GroupPointServiceImpl extends ServiceImpl<GroupPointMapper, GroupPointEntity> implements GroupPointService {

    @Override
    public Map<Long, String> getPointNames(Set<Long> ids) {
        return null;
    }
}
