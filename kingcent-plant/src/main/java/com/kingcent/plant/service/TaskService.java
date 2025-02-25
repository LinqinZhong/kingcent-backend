package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.TaskEntity;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface TaskService extends IService<TaskEntity> {
    Page<TaskEntity> getPage(Integer pageNum, Integer pageSize);
    Result<?> addOrUpdate(Long userId, TaskEntity taskEntity);
    Result<?> delete(Long id);

    Result<?> setStatus(Long userId, Long taskId, Integer status);

    TaskEntity detail(Long userId, Long taskId);
}
