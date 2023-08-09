package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.GroupEntity;
import com.kingcent.campus.shop.entity.vo.group.GroupLocationVo;
import com.kingcent.campus.shop.mapper.GroupMapper;
import com.kingcent.campus.shop.service.GroupService;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupEntity> implements GroupService {
    @Override
    public Map<Long, String> getGroupNames(Set<Long> ids) {
        return null;
    }

    @Override
    public Result<List<GroupLocationVo>> fetchNearbyGroups(Double longitude, Double latitude) {
        return null;
    }

    @Override
    public void initPointLocations() {

    }

    @Override
    public Result<?> fetchNearestGroup(Double lng, Double lat) {
        return null;
    }
}
