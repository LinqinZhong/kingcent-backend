package com.kingcent.plant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.common.result.Result;
import com.kingcent.plant.entity.MemberEntity;
import com.kingcent.plant.entity.SaplingEntity;
import com.kingcent.plant.entity.VarietyEntity;
import com.kingcent.plant.mapper.SaplingMapper;
import com.kingcent.plant.mapper.VarietyMapper;
import com.kingcent.plant.service.MemberService;
import com.kingcent.plant.service.SaplingService;
import com.kingcent.plant.service.VarietyService;
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
public class SaplingServiceImpl extends ServiceImpl<SaplingMapper, SaplingEntity> implements SaplingService {

    @Autowired
    private MemberService memberService;

    @Autowired
    private VarietyService varietyService;

    @Override
    public Page<SaplingEntity> getPage(Integer pageNum, Integer pageSize){
        Page<SaplingEntity> page = page(new Page<>(pageNum, pageSize));
        Set<Long> memberIds = new HashSet<>();
        Set<Long> varietyIds = new HashSet<>();
        for (SaplingEntity record : page.getRecords()) {
            if(record.getCreatorMemberId() != null) memberIds.add(record.getCreatorMemberId());
            if(record.getVarietyId() != null) varietyIds.add(record.getVarietyId());
        }
        Map<Long, MemberEntity> memberEntityMap = new HashMap<>();
        Map<Long, VarietyEntity> varietyEntityMap = new HashMap<>();
        if(!memberIds.isEmpty()){
            List<MemberEntity> list = memberService.list(new LambdaQueryWrapper<MemberEntity>()
                    .in(MemberEntity::getId, memberIds)
            );
            for (MemberEntity memberEntity : list) {
                memberEntityMap.put(memberEntity.getId(), memberEntity);
            }
        }

        if(!varietyIds.isEmpty()){
            List<VarietyEntity> list = varietyService.list(new LambdaQueryWrapper<VarietyEntity>()
                    .in(VarietyEntity::getId, varietyIds)
            );
            for (VarietyEntity varietyEntity : list) {
                varietyEntityMap.put(varietyEntity.getId(), varietyEntity);
            }
        }

        for (SaplingEntity record : page.getRecords()) {
            VarietyEntity varietyEntity = varietyEntityMap.get(record.getId());
            MemberEntity memberEntity = memberEntityMap.get(record.getCreatorMemberId());
            if(varietyEntity != null) {
                record.setVarietyName(varietyEntity.getName());
                record.setThumb(varietyEntity.getThumb());
            }
            if(memberEntity != null) record.setCreatorMemberId(memberEntity.getId());
        }

        return page;
    }

    @Override
    @Transactional
    public Result<?> addOrUpdate(Long userId, SaplingEntity entity){
        Result<MemberEntity> memberIdRes = memberService.getByUserId(userId);
        if(!memberIdRes.getSuccess()){
            return memberIdRes;
        }
        entity.setCreatorMemberId(memberIdRes.getData().getId());
        entity.setCreateTime(LocalDateTime.now());
        saveOrUpdate(entity);
        return Result.success();
    }

    @Override
    public Result<?> delete(Long id) {
        removeById(id);
        return Result.success();
    }
}
