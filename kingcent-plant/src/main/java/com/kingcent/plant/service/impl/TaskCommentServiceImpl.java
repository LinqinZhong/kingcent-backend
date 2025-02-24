package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.exception.KingcentSystemException;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.MemberEntity;
import com.kingcent.plant.entity.TaskCommentEntity;
import com.kingcent.plant.mapper.TaskCommentMapper;
import com.kingcent.plant.service.MemberService;
import com.kingcent.plant.service.TaskCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;



@Service
public class TaskCommentServiceImpl extends ServiceImpl<TaskCommentMapper, TaskCommentEntity> implements TaskCommentService {

    @Autowired
    private MemberService memberService;
    @Override
    public Page<TaskCommentEntity> getPage(Long taskId, Integer pageNum, Integer pageSize) throws KingcentSystemException {
        LambdaQueryWrapper<TaskCommentEntity> wrapper = new LambdaQueryWrapper<TaskCommentEntity>()
                .eq(TaskCommentEntity::getTaskId, taskId)
                .orderBy(true,true,TaskCommentEntity::getCreateTime);
        Page<TaskCommentEntity> pager = new Page<>(pageNum,pageSize,true);
        Page<TaskCommentEntity> page = page(pager, wrapper);
        List<TaskCommentEntity> records = page.getRecords();
        if(!records.isEmpty()){
            Set<Long> memberIds = new HashSet<>();
            for (TaskCommentEntity record : records) {
                memberIds.add(record.getMemberId());
            }
            Map<Long, MemberEntity> memberMap = new HashMap<>();
            List<MemberEntity> memberEntities = memberService.listByIds(memberIds);
            for (MemberEntity memberEntity : memberEntities) {
                memberMap.put(memberEntity.getId(),memberEntity);
            }
            for (TaskCommentEntity record : records) {
                MemberEntity memberEntity = memberMap.get(record.getMemberId());
                if(memberEntity != null){
                    record.setNickname(memberEntity.getName());
                }
            }
        }
        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(Long userId, TaskCommentEntity taskCommentEntity) {
        Result<MemberEntity> memberResult = memberService.getByUserId(userId);
        if (!memberResult.getSuccess()){
            return memberResult;
        }
        MemberEntity member = memberResult.getData();
        if(taskCommentEntity.getId() == null){
            taskCommentEntity.setUserId(userId);
            taskCommentEntity.setCreateTime(LocalDateTime.now());
            taskCommentEntity.setMemberId(member.getId());

        }else{
            if(!Objects.equals(taskCommentEntity.getMemberId(), member.getId())){
                return Result.fail("不能修改其他人发送的评论");
            }
        }
        taskCommentEntity.setUpdateTime(LocalDateTime.now());
        if(saveOrUpdate(taskCommentEntity)) {
            return Result.success();
        }
        return Result.fail("评论失败");
    }

    @Override
    public Result<?> delete(Long userId, Long taskCommentId) {
        return null;
    }
}
