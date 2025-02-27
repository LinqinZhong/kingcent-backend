package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.*;
import com.kingcent.plant.mapper.TaskMapper;
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
    public Page<TaskEntity> getPage(
            Long userId,
                                    Integer pageNum,
                                    Integer pageSize,
                                    Long planId,
                                    String memberIds0,
                                    String landIds0,
                                    String nameLike,
            List<Integer> status,
            List<Integer> type,
                                    String startTimeFrom0,
                                    String startTimeEnd0,
                                    String endTimeFrom0,
                                    String endTimeEnd0
    ) throws KingcentSystemException {
        Result<MemberEntity> memberResult = memberService.getByUserId(userId);
        if(!memberResult.getSuccess()){
            throw new KingcentSystemException(memberResult.getMessage());
        }
        List<Long> memberIdList = null;
        if(memberIds0 != null) {
            try {
                memberIdList = memberIds0.equals("current")
                        ? List.of(memberResult.getData().getId())
                        : Arrays.stream(memberIds0.split(",")).map(Long::parseLong).toList();
            } catch (Exception ignored) {}
        }
        List<Long> landIdList = null;
        if(memberIds0 != null) {
            try {
                landIdList = Arrays.stream(landIds0.split(",")).map(Long::parseLong).toList();
            } catch (Exception ignored) {}
        }
        LocalDateTime startTimeFrom = null;
        LocalDateTime startTimeEnd = null;
        LocalDateTime endTimeFrom = null;
        LocalDateTime endTimeEnd = null;



        if(pageNum == null || pageNum <= 0) pageNum = 1;
        if(pageSize == null || pageSize <= 0) pageSize = 10;
        Integer count = baseMapper.selectCount(
                planId,
                memberIdList,
                landIdList,
                nameLike,
                status,
                type,
                startTimeFrom,
                startTimeEnd,
                endTimeFrom,
                endTimeEnd
        );
        Page<TaskEntity> taskPage = new Page<>(0, 0);
        if(count == null || count == 0) return taskPage;
        List<TaskEntity> taskEntityList = baseMapper.selectPage(
                pageSize*(pageNum-1),
                pageSize,
                planId,
                memberIdList,
                landIdList,
                nameLike,
                status,
                type,
                startTimeFrom,
                startTimeEnd,
                endTimeFrom,
                endTimeEnd
        );
        taskPage.setTotal(count);
        taskPage.setRecords(taskEntityList);
        if(taskEntityList.isEmpty()) return taskPage;

        List<Long> taskIds = new ArrayList<>();
        Set<Long> planIds = new HashSet<>();
        for (TaskEntity taskEntity : taskEntityList) {
            taskIds.add(taskEntity.getId());
            planIds.add(taskEntity.getPlanId());
        }


        List<TaskLandEntity> landOfTaskList = taskLandService.list(new LambdaQueryWrapper<TaskLandEntity>().in(TaskLandEntity::getTaskId, taskIds));
        if (!landOfTaskList.isEmpty()) {
            Set<Long> totalLandIds = new HashSet<>();
            Map<Long, List<Long>> landOfTaskMap = new HashMap<>();


            Set<Long> totalMemberIds = new HashSet<>();

            for (TaskLandEntity taskLandEntity : landOfTaskList) {
                List<Long> landIdsOfTask = landOfTaskMap.computeIfAbsent(taskLandEntity.getTaskId(), (r) -> new ArrayList<>());
                totalLandIds.add(taskLandEntity.getLandId());
                landIdsOfTask.add(taskLandEntity.getLandId());
            }
            Map<Long, LandEntity> landEntityMap = new HashMap<>();
            List<LandEntity> landList = landService.list(new LambdaQueryWrapper<LandEntity>().in(LandEntity::getId, totalLandIds));
            for (LandEntity landEntity : landList) {
                landEntityMap.put(landEntity.getId(), landEntity);
            }
            taskEntityList.forEach(task -> {
                List<String> landNames = new ArrayList<>();
                List<String> landIdsOfTask = new ArrayList<>();
                List<Long> landIdListOfTask = landOfTaskMap.get(task.getId());
                if (landIdListOfTask != null) {
                    for (Long landId : landIdListOfTask) {
                        LandEntity landEntity = landEntityMap.get(landId);
                        if (landEntity != null) {
                            landNames.add(landEntity.getName());
                            landIdsOfTask.add(landId + "");
                        }
                    }
                }
                totalMemberIds.add(task.getCreatorMemberId());
                task.setLandNames(String.join(",", landNames));
                task.setLandIds(String.join(",", landIdsOfTask));

            });

            List<TaskMemberEntity> memberOfTask = taskMemberService.list(
                    new LambdaQueryWrapper<TaskMemberEntity>().in(
                            TaskMemberEntity::getTaskId,
                            taskIds
                    )
            );

            if (!memberOfTask.isEmpty()) {
                Map<Long, List<Long>> memberOfTaskMap = new HashMap<>();
                for (TaskMemberEntity taskMemberEntity : memberOfTask) {
                    List<Long> landIdsOfTask = memberOfTaskMap.computeIfAbsent(taskMemberEntity.getTaskId(), (r) -> new ArrayList<>());
                    totalMemberIds.add(taskMemberEntity.getMemberId());
                    landIdsOfTask.add(taskMemberEntity.getMemberId());
                }
                Map<Long, MemberEntity> memberEntityMap = new HashMap<>();
                List<MemberEntity> memberList = memberService.list(new LambdaQueryWrapper<MemberEntity>().in(MemberEntity::getId, totalMemberIds));
                for (MemberEntity member : memberList) {
                    memberEntityMap.put(member.getId(), member);
                }
                taskEntityList.forEach(task -> {
                    List<String> memberNames = new ArrayList<>();
                    List<String> memberIds = new ArrayList<>();
                    List<Long> memberIdListOfTask = memberOfTaskMap.get(task.getId());
                    if (memberIdListOfTask != null) {
                        for (Long memberId : memberIdListOfTask) {
                            MemberEntity member = memberEntityMap.get(memberId);
                            if (member != null) {
                                memberNames.add(member.getName());
                                memberIds.add(memberId + "");
                            }
                        }
                    }
                    MemberEntity memberEntity = memberEntityMap.get(task.getCreatorMemberId());
                    if(memberEntity != null){
                        task.setCreatorMemberName(memberEntity.getName());
                    }
                    task.setMemberNames(String.join(",", memberNames));
                    task.setMemberIds(String.join(",", memberIds));

                });

            }
        }


        if(!planIds.isEmpty()) {
            Map<Long, PlanEntity> planEntityMap = new HashMap<>();
            List<PlanEntity> planEntities = planService.list(new LambdaQueryWrapper<PlanEntity>().in(PlanEntity::getId, planIds));
            planEntities.forEach((p -> {
                planEntityMap.put(p.getId(), p);
            }));
            taskEntityList.forEach(r -> {
                PlanEntity planEntity = planEntityMap.get(r.getPlanId());
                if (planEntity != null) r.setPlanName(planEntity.getName());
            });
        }
        return taskPage;
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
            if(taskEntity.getStatus() == null) taskEntity.setStatus(0);
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

    @Override
    public TaskEntity detail(Long userId, Long taskId) {
        TaskEntity task = getById(taskId);
        Map<Long,MemberEntity> memberEntityMap = new HashMap<>();
        Set<Long> totalMemberIds = new HashSet<>();
        totalMemberIds.add(task.getCreatorMemberId());
        List<TaskMemberEntity> list = taskMemberService.list(new LambdaQueryWrapper<TaskMemberEntity>().eq(TaskMemberEntity::getTaskId, task.getId()));
        List<Long> memberIds = list.stream().map(TaskMemberEntity::getMemberId).toList();
        totalMemberIds.addAll(memberIds);
        System.out.println(memberIds);
        if(!totalMemberIds.isEmpty()){
            List<MemberEntity> memberEntities = memberService.listByIds(totalMemberIds);
            for (MemberEntity memberEntity : memberEntities) {
                memberEntityMap.put(memberEntity.getId(), memberEntity);
            }
            List<String> memberNames = new ArrayList<>();
            for (Long memberId : memberIds) {
                MemberEntity member = memberEntityMap.get(memberId);
                if(member != null) memberNames.add(member.getName());
            }
            task.setMemberNames(String.join(",",memberNames));
            MemberEntity creator = memberEntityMap.get(task.getCreatorMemberId());
            if(creator != null){
                task.setCreatorMemberName(creator.getName());
            }
        }
        if(task.getPlanId() != null) {
            PlanEntity plan = planService.getOne(new LambdaQueryWrapper<PlanEntity>().eq(PlanEntity::getId, task.getPlanId()));
            task.setPlanName(plan.getName());
        }
        List<Long> landIds = taskLandService.list(
                new LambdaQueryWrapper<TaskLandEntity>()
                        .eq(TaskLandEntity::getTaskId, task.getId()
                        )
                )
                .stream()
                .map(TaskLandEntity::getLandId)
                .toList();
        if(!landIds.isEmpty()) {
            List<String> landNames = landService.listByIds(landIds).stream().map(LandEntity::getName).toList();
            task.setLandNames(String.join(",",landNames));
        }
        return task;
    }
}
