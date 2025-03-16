package com.kingcent.plant.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.*;
import com.kingcent.plant.mapper.LandMapper;
import com.kingcent.plant.mapper.PlanMapper;
import com.kingcent.plant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:07
 */
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, PlanEntity> implements PlanService {


    @Lazy
    @Autowired
    private TaskService taskService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TaskMemberService taskMemberService;

    @Override
    public Page<PlanEntity> getPage(Long userId, Integer pageNum, Integer pageSize) throws KingcentSystemException {


        Result<MemberEntity> memberResult = memberService.getByUserId(userId);
        if(!memberResult.getSuccess()){
            throw new KingcentSystemException(memberResult.getMessage());
        }

        MemberEntity currentMember= memberResult.getData();

        Page<PlanEntity> page = page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<PlanEntity>()
                        .select(
                                PlanEntity::getName,
                                PlanEntity::getCreatorId,
                                PlanEntity::getNo,
                                PlanEntity::getId,
                                PlanEntity::getEndTime,
                                PlanEntity::getCreateTime,
                                PlanEntity::getStartTime,
                                PlanEntity::getStatus,
                                PlanEntity::getReviewerId
                        )
        );
        Set<Long> memberIds = new HashSet<>();
        if (!page.getRecords().isEmpty()) {
            for (PlanEntity record : page.getRecords()) {
                memberIds.add(record.getReviewerId());
                memberIds.add(record.getCreatorId());
            }
        }

        if(!memberIds.isEmpty()){
            Map<Long, MemberEntity> memberEntityMap = new HashMap<>();
            List<MemberEntity> memberEntities = memberService.listByIds(memberIds);
            for (MemberEntity memberEntity : memberEntities) {
                memberEntityMap.put(memberEntity.getId(),memberEntity);
            }
            for (PlanEntity record : page.getRecords()) {
                MemberEntity reviewer = memberEntityMap.get(record.getReviewerId());
                if(reviewer != null) record.setReviewerName(reviewer.getName());
                MemberEntity creator = memberEntityMap.get(record.getCreatorId());
                if(creator != null) record.setCreatorName(creator.getName());
                record.setReviewable(record.getReviewerId().equals(currentMember.getId()));
                record.setEditable(record.getCreatorId().equals(currentMember.getId()) && record.getStatus().equals(9));
            }
        }
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(Long userId, PlanEntity planEntity){



        Result<MemberEntity> memberResult = memberService.getByUserId(userId);
        if(!memberResult.getSuccess()){
            return memberResult;
        }

        if(planEntity.getStartTime() == null || planEntity.getEndTime() == null){
            return Result.fail("开始时间和结束时间不能为空");
        }
        if(planEntity.getStartTime().isBefore(LocalDateTime.now().plusDays(3))){
            return Result.fail("计划最早只能在3天后开始");
        }
        if(planEntity.getStartTime().isAfter(planEntity.getEndTime())){
            return Result.fail("开始时间不能晚于结束时间");
        }
        boolean isUpdate = planEntity.getId() != null;
        if(!isUpdate) {
            planEntity.setStatus(0);
            planEntity.setCreateTime(LocalDateTime.now());
            planEntity.setCreatorId(memberResult.getData().getId());
        }
        planEntity.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(planEntity);

        if(!isUpdate){
            // 创建审批任务
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setStatus(1);
            taskEntity.setContent(planEntity.getReason());
            taskEntity.setName("对计划["+planEntity.getNo()+"]进行审批");
            taskEntity.setPlanId(planEntity.getId());
            taskEntity.setType(8);
            taskEntity.setStatus(0);
            taskEntity.setStartTime(LocalDateTime.now());
            taskEntity.setEndTime(planEntity.getStartTime().plusDays(-2));
            taskService.addOrUpdate(userId, taskEntity);

            TaskMemberEntity taskMemberEntity = new TaskMemberEntity(taskEntity.getId(),planEntity.getReviewerId());
            taskMemberService.save(taskMemberEntity);
        }

        return Result.success();
    }

    @Override
    public Result<?> delete(Long planId) {
        removeById(planId);
        return Result.success();
    }

    @Override
    public PlanEntity detail(Long userId, Long planId) throws KingcentSystemException {

        Result<MemberEntity> memberResult = memberService.getByUserId(userId);
        if(!memberResult.getSuccess()){
            throw new KingcentSystemException(memberResult.getMessage());
        }

        MemberEntity currentMember= memberResult.getData();
        PlanEntity plan = getById(planId);
        plan.setReviewable(currentMember.getId().equals(plan.getReviewerId()));
        plan.setEditable(plan.getCreatorId().equals(currentMember.getId()) && plan.getStatus().equals(-2));

        return plan;
    }

    @Transactional
    @Override
    public Result<?> reject(Long userId, Long planId, JSONObject data) {
        Result<MemberEntity> memberResult = memberService.getByUserId(userId);
        if(!memberResult.getSuccess()){
            return memberResult;
        }
        PlanEntity plan = getById(planId);
        if(!plan.getReviewerId().equals(memberResult.getData().getId())){
            return Result.fail("没有权限对此计划进行审批");
        }
        Boolean resubmit = data.getBoolean("resubmit");
        String reason = data.getString("reason");
        String suggestion = data.getString("suggestion");
        if(resubmit == null){
            return Result.fail("缺少字段resubmit");
        }
        if(reason == null || reason.trim().isEmpty()){
            return Result.fail("请填写拒绝原因");
        }
        Long taskId = data.getLong("taskId");
        TaskEntity reviewTask = taskService.getById(taskId);
        if(!Objects.equals(reviewTask.getPlanId(), planId)){
            return Result.fail("计划不存在");
        }
        long count = taskMemberService.count(new LambdaQueryWrapper<TaskMemberEntity>()
                .eq(TaskMemberEntity::getMemberId, memberResult.getData().getId())
                .eq(TaskMemberEntity::getTaskId, taskId)
        );
        if(count == 0){
            return Result.fail("无权限");
        }

        // 更新任务状态为已经完成
        reviewTask.setStatus(4);
        taskService.updateById(reviewTask);

        plan.setStatus(-2);
        updateById(plan);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setCreatorMemberId(memberResult.getData().getId());
        taskEntity.setName("对计划["+plan.getNo()+"]进行修改");
        taskEntity.setStatus(0);
        taskEntity.setType(9);
        taskEntity.setContent("拒绝原因：\n"
                +reason
                +"\n\n修改意见：\n"
                +(suggestion != null && suggestion.trim().length() > 0
                ? suggestion
                : "无"
        ));
        taskEntity.setCreateTime(LocalDateTime.now());
        taskEntity.setUpdateTime(LocalDateTime.now());
        taskEntity.setPlanId(planId);
        taskService.save(taskEntity);
        TaskMemberEntity taskMemberEntity = new TaskMemberEntity(taskEntity.getId(),plan.getCreatorId());
        taskMemberService.save(taskMemberEntity);


        return Result.success();
    }

    @Override
    public Result<?> approve(Long userId, Long planId, JSONObject data) {
        Result<MemberEntity> memberResult = memberService.getByUserId(userId);
        if(!memberResult.getSuccess()){
            return memberResult;
        }
        PlanEntity plan = getById(planId);
        if(!plan.getReviewerId().equals(memberResult.getData().getId())){
            return Result.fail("没有权限对此计划进行审批");
        }

        Long taskId = data.getLong("taskId");
        TaskEntity reviewTask = taskService.getById(taskId);
        if(!Objects.equals(reviewTask.getPlanId(), planId)){
            return Result.fail("计划不存在");
        }
        long count = taskMemberService.count(new LambdaQueryWrapper<TaskMemberEntity>()
                .eq(TaskMemberEntity::getMemberId, memberResult.getData().getId())
                .eq(TaskMemberEntity::getTaskId, taskId)
        );
        if(count == 0){
            return Result.fail("无权限");
        }
        reviewTask.setStatus(4);
        taskService.updateById(reviewTask);
        plan.setStatus(1);
        updateById(plan);
        return Result.success();
    }
}
