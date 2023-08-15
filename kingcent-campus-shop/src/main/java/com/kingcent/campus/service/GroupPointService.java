package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.GroupPointEntity;

import java.util.Map;
import java.util.Set;

public interface GroupPointService extends IService<GroupPointEntity> {
    Map<Long, String> getPointNames(Set<Long> ids);
}
