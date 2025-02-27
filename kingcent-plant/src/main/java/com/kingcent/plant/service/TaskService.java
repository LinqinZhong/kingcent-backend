package com.kingcent.plant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.TaskEntity;

import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
public interface TaskService extends IService<TaskEntity> {
    Result<?> addOrUpdate(Long userId, TaskEntity taskEntity);
    Result<?> delete(Long id);

    Result<?> setStatus(Long userId, Long taskId, Integer status);

    TaskEntity detail(Long userId, Long taskId);

    Page<TaskEntity> getPage(Long userId, Integer pageNum, Integer pageSize, Long planId, String memberIds, String landIds, String nameLike, List<Integer> status, List<Integer> type, String startTimeFrom, String startTimeEnd, String endTimeFrom, String endTimeEnd) throws KingcentSystemException;
}
