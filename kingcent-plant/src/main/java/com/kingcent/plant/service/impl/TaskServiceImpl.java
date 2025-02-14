package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.entity.PlanEntity;
import com.kingcent.plant.entity.TaskEntity;
import com.kingcent.plant.entity.TaskLandEntity;
import com.kingcent.plant.mapper.PlanMapper;
import com.kingcent.plant.mapper.TaskMapper;
import com.kingcent.plant.service.LandService;
import com.kingcent.plant.service.PlanService;
import com.kingcent.plant.service.TaskLandService;
import com.kingcent.plant.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, TaskEntity> implements TaskService {

    @Autowired
    private TaskLandService taskLandService;

    @Autowired
    private PlanService planService;

    @Autowired
    private LandService landService;

    @Override
    public Page<TaskEntity> getPage(Integer pageNum, Integer pageSize){
        Page<TaskEntity> page = page(new Page<>(pageNum, pageSize));
        if(page.getSize() == 0) return  page;
        Set<Long> planIds = new HashSet<>();
        Set<Long> taskIds = new HashSet<>();
        page.getRecords().forEach(r -> {
            taskIds.add(r.getId());
            planIds.add(r.getPlanId());
        });
        if(!taskIds.isEmpty()) {
            List<TaskLandEntity> landOfTaskList = taskLandService.list(new LambdaQueryWrapper<TaskLandEntity>().in(TaskLandEntity::getTaskId, taskIds));
            if (!landOfTaskList.isEmpty()) {
                Set<Long> totalLandIds = new HashSet<>();
                Map<Long, List<Long>> landOfTaskMap = new HashMap<>();
                for (TaskLandEntity taskLandEntity : landOfTaskList) {
                    List<Long> landIds = landOfTaskMap.computeIfAbsent(taskLandEntity.getTaskId(), (r) -> new ArrayList<>());
                    totalLandIds.add(taskLandEntity.getLandId());
                    landIds.add(taskLandEntity.getLandId());
                }
                Map<Long, LandEntity> landEntityMap = new HashMap<>();
                List<LandEntity> landList = landService.list(new LambdaQueryWrapper<LandEntity>().in(LandEntity::getId, totalLandIds));
                for (LandEntity landEntity : landList) {
                    landEntityMap.put(landEntity.getId(), landEntity);
                }
                page.getRecords().forEach(task -> {
                    List<String> landNames = new ArrayList<>();
                    List<String> landIds = new ArrayList<>();
                    List<Long> landIdList = landOfTaskMap.get(task.getId());
                    if (landIdList != null) {
                        for (Long landId : landIdList) {
                            LandEntity landEntity = landEntityMap.get(landId);
                            if (landEntity != null) {
                                landNames.add(landEntity.getName());
                                landIds.add(landId + "");
                            }
                        }
                    }

                    task.setLandNames(String.join(",", landNames));
                    task.setLandIds(String.join(",", landIds));

                });

            }
        }


        if(!planIds.isEmpty()) {
            Map<Long, PlanEntity> planEntityMap = new HashMap<>();
            List<PlanEntity> planEntities = planService.list(new LambdaQueryWrapper<PlanEntity>().in(PlanEntity::getId, planIds));
            planEntities.forEach((p -> {
                planEntityMap.put(p.getId(), p);
            }));
            page.getRecords().forEach(r -> {
                PlanEntity planEntity = planEntityMap.get(r.getPlanId());
                if (planEntity != null) r.setPlanName(planEntity.getName());
            });
        }
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(TaskEntity taskEntity){
        boolean isUpdate = taskEntity.getId() != null;
        if(!isUpdate) taskEntity.setCreateTime(LocalDateTime.now());
        taskEntity.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(taskEntity);


        Set<Long> currentLandIds = new HashSet<>();
        Set<Long> deletedLandIds = new HashSet<>();
        LambdaQueryWrapper<TaskLandEntity> currentLandsWrapper = new LambdaQueryWrapper<TaskLandEntity>()
                .eq(TaskLandEntity::getTaskId, taskEntity.getId());


        // 将所有土地记录为删除的土地
        if(isUpdate) {
            List<TaskLandEntity> currentLands = taskLandService.list(currentLandsWrapper);
            for (TaskLandEntity currentLand : currentLands) {
                deletedLandIds.add(currentLand.getLandId());
                currentLandIds.add(currentLand.getLandId());
            }
        }

        // 新增土地
        if(taskEntity.getLandIds() != null) {
            String[] landIds = taskEntity.getLandIds().split(",");

            // 仍然保留的土地不用删除
            for (String landId : landIds) deletedLandIds.remove(Long.parseLong(landId));


            if (landIds.length > 0) {
                List<TaskLandEntity> taskLandEntityList = new ArrayList<>();
                for (String landId0 : landIds) {
                    Long landId = Long.parseLong(landId0);
                    // 新增的时候排除有的
                    if(currentLandIds.contains(landId)){
                        continue;
                    }
                    taskLandEntityList.add(
                            new TaskLandEntity(taskEntity.getId(), landId)
                    );
                }
                taskLandService.saveBatch(taskLandEntityList);
            }

        }

        // 删除不存在的土地
        if(!deletedLandIds.isEmpty()) {
            taskLandService.remove(currentLandsWrapper.in(TaskLandEntity::getLandId, deletedLandIds));
        }
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }
}
