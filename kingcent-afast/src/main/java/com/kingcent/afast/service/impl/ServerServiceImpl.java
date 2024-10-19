package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.entity.ServerEntity;
import com.kingcent.afast.mapper.ServerMapper;
import com.kingcent.afast.service.ServerService;
import com.kingcent.common.entity.result.Result;
import org.springframework.stereotype.Service;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
@Service
public class ServerServiceImpl extends ServiceImpl<ServerMapper, ServerEntity> implements ServerService {
    @Override
    public void create(Long userId, Long groupId, String name) {
        ServerEntity server = new ServerEntity();
        server.setName(name);
        save(server);
    }

    @Override
    public Result<Page<ServerEntity>> list(Long userId, Long pageSize, Long pageNum, Long[] groupIds) {
        LambdaQueryWrapper<ServerEntity> wrapper = new LambdaQueryWrapper<>();
        return Result.success("",page(new Page<>(pageNum,pageSize),wrapper));
    }
}
