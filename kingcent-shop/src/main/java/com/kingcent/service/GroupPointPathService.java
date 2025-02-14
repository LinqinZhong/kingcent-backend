package com.kingcent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.common.shop.entity.GroupPointPathEntity;

public interface GroupPointPathService extends IService<GroupPointPathEntity> {
    Result<?> getPath();
}
