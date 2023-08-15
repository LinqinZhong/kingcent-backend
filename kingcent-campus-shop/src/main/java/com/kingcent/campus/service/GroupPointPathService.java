package com.kingcent.campus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.campus.shop.entity.GroupPointPathEntity;
import com.kingcent.campus.common.entity.result.Result;

public interface GroupPointPathService extends IService<GroupPointPathEntity> {
    Result<?> getPath();
}
