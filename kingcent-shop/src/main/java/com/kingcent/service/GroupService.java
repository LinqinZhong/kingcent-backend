package com.kingcent.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.entity.result.Result;
import com.kingcent.common.shop.entity.GroupEntity;
import com.kingcent.common.shop.entity.vo.group.GroupLocationVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GroupService extends IService<GroupEntity> {
    Map<Long, String> getGroupNames(Set<Long> ids);

//    Result<List<GroupLocationVo>> fetchNearbyGroups(Double longitude, Double latitude);

    void initPointLocations();

//    Result<?> fetchNearestGroup(Double lng, Double lat);

    Result<List<GroupLocationVo>> ofSite(Long siteId);
}
