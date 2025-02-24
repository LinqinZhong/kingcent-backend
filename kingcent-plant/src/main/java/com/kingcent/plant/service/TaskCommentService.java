package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.TaskCommentEntity;

public interface TaskCommentService extends IService<TaskCommentEntity> {
    Page<TaskCommentEntity> getPage(Long taskId, Integer pageNum, Integer pageSize) throws KingcentSystemException;

    Result<?> addOrUpdate(Long userId, TaskCommentEntity taskCommentEntity);

    Result<?> delete(Long userId, Long taskCommentId);
}
