package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.common.user.entity.UserEntity;
import com.kingcent.plant.entity.*;
import com.kingcent.plant.mapper.PlanMapper;
import com.kingcent.plant.mapper.TaskMapper;
import com.kingcent.plant.mapper.TaskMemberMapper;
import com.kingcent.plant.service.*;
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
    private TaskMemberService taskMemberService;

    @Autowired
    private PlanService planService;

    @Autowired
    private LandService landService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TaskCommentService taskCommentService;

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

                List<TaskMemberEntity> memberOfTask = taskMemberService.list(new LambdaQueryWrapper<TaskMemberEntity>().in(TaskMemberEntity::getTaskId, taskIds));
                if (!memberOfTask.isEmpty()) {
                    Set<Long> totalMemberIds = new HashSet<>();
                    Map<Long, List<Long>> memberOfTaskMap = new HashMap<>();
                    for (TaskMemberEntity taskMemberEntity : memberOfTask) {
                        List<Long> landIds = memberOfTaskMap.computeIfAbsent(taskMemberEntity.getTaskId(), (r) -> new ArrayList<>());
                        totalMemberIds.add(taskMemberEntity.getMemberId());
                        landIds.add(taskMemberEntity.getMemberId());
                    }
                    Map<Long, MemberEntity> memberEntityMap = new HashMap<>();
                    List<MemberEntity> memberList = memberService.list(new LambdaQueryWrapper<MemberEntity>().in(MemberEntity::getId, totalMemberIds));
                    for (MemberEntity member : memberList) {
                        memberEntityMap.put(member.getId(), member);
                    }
                    page.getRecords().forEach(task -> {
                        List<String> memberNames = new ArrayList<>();
                        List<String> memberIds = new ArrayList<>();
                        List<Long> memberIdList = memberOfTaskMap.get(task.getId());
                        if (memberIdList != null) {
                            for (Long memberId : memberIdList) {
                                MemberEntity member = memberEntityMap.get(memberId);
                                if (member != null) {
                                    memberNames.add(member.getName());
                                    memberIds.add(memberId + "");
                                }
                            }
                        }

                        task.setMemberNames(String.join(",", memberNames));
                        task.setMemberIds(String.join(",", memberIds));

                    });

                }
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
    public Result<?> addOrUpdate(Long userId, TaskEntity taskEntity){

        Result<MemberEntity> memberResult = memberService.getByUserId(userId);
        if(!memberResult.getSuccess()){
            return memberResult;
        }

        boolean isUpdate = taskEntity.getId() != null;
        if(!isUpdate) {
            taskEntity.setStatus(0);
            taskEntity.setCreateTime(LocalDateTime.now());
            taskEntity.setCreatorMemberId(memberResult.getData().getId());
        }
        taskEntity.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(taskEntity);


        Set<Long> currentLandIds = new HashSet<>();
        Set<Long> deletedLandIds = new HashSet<>();
        Set<Long> currentMemberIds = new HashSet<>();
        Set<Long> deletedMemberIds = new HashSet<>();
        LambdaQueryWrapper<TaskLandEntity> currentLandsWrapper = new LambdaQueryWrapper<TaskLandEntity>()
                .eq(TaskLandEntity::getTaskId, taskEntity.getId());

        LambdaQueryWrapper<TaskMemberEntity> currentMemberWrapper = new LambdaQueryWrapper<TaskMemberEntity>()
                .eq(TaskMemberEntity::getTaskId, taskEntity.getId());


        if(isUpdate) {
            List<TaskLandEntity> currentLands = taskLandService.list(currentLandsWrapper);
            for (TaskLandEntity currentLand : currentLands) {
                deletedLandIds.add(currentLand.getLandId());
                currentLandIds.add(currentLand.getLandId());
            }

            List<TaskMemberEntity> currentMembers = taskMemberService.list(currentMemberWrapper);
            for (TaskMemberEntity taskMemberEntity : currentMembers) {
                deletedMemberIds.add(taskMemberEntity.getMemberId());
                currentMemberIds.add(taskMemberEntity.getMemberId());
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

        // 新增成员
        if(taskEntity.getMemberIds() != null) {
            String[] memberIds = taskEntity.getMemberIds().split(",");

            // 仍然保留的成员不用删除
            for (String memberId : memberIds) deletedMemberIds.remove(Long.parseLong(memberId));


            if (memberIds.length > 0) {
                List<TaskMemberEntity> taskMemberEntityList = new ArrayList<>();
                for (String memberId0 : memberIds) {
                    Long memberId = Long.parseLong(memberId0);
                    // 新增的时候排除有的
                    if(currentMemberIds.contains(memberId)){
                        continue;
                    }
                    taskMemberEntityList.add(
                            new TaskMemberEntity(taskEntity.getId(), memberId)
                    );
                }
                taskMemberService.saveBatch(taskMemberEntityList);
            }

        }


        // 删除不存在的土地
        if(!deletedLandIds.isEmpty()) {
            taskLandService.remove(currentLandsWrapper.in(TaskLandEntity::getLandId, deletedLandIds));
        }

        // 删除不存在的成员
        if(!deletedMemberIds.isEmpty()) {
            taskMemberService.remove(currentMemberWrapper.in(TaskMemberEntity::getMemberId, deletedMemberIds));
        }
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<?> setStatus(Long userId, Long taskId, Integer status) {
        Result<MemberEntity> memberResult = memberService.getByUserId(userId);
        if(!memberResult.getSuccess()){
            return memberResult;
        }
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setStatus(status);
        taskEntity.setId(taskId);
        updateById(taskEntity);
        TaskCommentEntity taskCommentEntity = new TaskCommentEntity();
        taskCommentEntity.setTaskId(taskId);
        taskCommentEntity.setMemberId(memberResult.getData().getId());
        String content = null;
        switch (status){
            case 1 -> content = "开启了任务";
            case 2 -> content = "任务已交付，请验收！";
            case 3 -> content = "任务未通过";
            case 4 -> content = "任务已完成";
            case -1 -> content = "暂停了任务";
        };
        if(content != null){
            taskCommentEntity.setContent(content);
            taskCommentService.addOrUpdate(userId,taskCommentEntity);
        }
        return Result.success();
    }
}
