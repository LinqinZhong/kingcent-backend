package com.kingcent.campus.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.campus.common.entity.result.Result;
import com.kingcent.campus.shop.entity.GroupPointPathEntity;
import com.kingcent.campus.shop.mapper.GroupPointPathMapper;
import com.kingcent.campus.shop.service.GroupPointPathService;

/**
 * @author rainkyzhong
 * @date 2023/8/8 1:12
 */
public class GroupPointPathServiceImpl extends ServiceImpl<GroupPointPathMapper, GroupPointPathEntity> implements GroupPointPathService {

    @Override
    public Result<?> getPath() {
        return null;
    }
}
