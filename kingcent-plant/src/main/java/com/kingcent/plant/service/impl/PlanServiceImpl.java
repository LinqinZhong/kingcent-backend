package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.LandEntity;
import com.kingcent.plant.entity.MemberEntity;
import com.kingcent.plant.entity.PlanEntity;
import com.kingcent.plant.entity.TaskEntity;
import com.kingcent.plant.mapper.LandMapper;
import com.kingcent.plant.mapper.PlanMapper;
import com.kingcent.plant.service.LandService;
import com.kingcent.plant.service.MemberService;
import com.kingcent.plant.service.PlanService;
import com.kingcent.plant.service.TaskService;
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

    @Override
    public Page<PlanEntity> getPage(Integer pageNum, Integer pageSize){
        Page<PlanEntity> page = page(new Page<>(pageNum, pageSize));
        Set<Long> memberIds = new HashSet<>();
        if (!page.getRecords().isEmpty()) {
            for (PlanEntity record : page.getRecords()) {
                memberIds.add(record.getReviewerId());
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
            }
        }
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(Long userId, PlanEntity planEntity){
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
            planEntity.setStatus(1);
            planEntity.setCreateTime(LocalDateTime.now());
        }
        planEntity.setUpdateTime(LocalDateTime.now());
        saveOrUpdate(planEntity);

        // 创建审批任务
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setStatus(1);
        taskEntity.setName("对计划["+planEntity.getNo()+"]进行审批");
        taskEntity.setPlanId(planEntity.getId());
        taskEntity.setType(8);
        taskEntity.setStartTime(LocalDateTime.now());
        taskEntity.setEndTime(planEntity.getStartTime().plusDays(-3));
        taskService.addOrUpdate(userId, taskEntity);

        return Result.success();
    }

    @Override
    public Result<?> delete(Long planId) {
        removeById(planId);
        return Result.success();
    }
}
