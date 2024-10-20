package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.dto.ProjectDaoDto;
import com.kingcent.afast.dto.ProjectServiceDto;
import com.kingcent.afast.entity.*;
import com.kingcent.afast.mapper.ProjectDaoMapper;
import com.kingcent.afast.mapper.ProjectServiceMapper;
import com.kingcent.afast.service.ProjectDaoService;
import com.kingcent.afast.service.ProjectEntityService;
import com.kingcent.afast.service.ProjectServiceFuncService;
import com.kingcent.afast.service.ProjectServiceService;
import com.kingcent.afast.utils.ProjectDaoUtil;
import com.kingcent.afast.utils.ProjectServiceUtil;
import com.kingcent.afast.vo.ProjectDaoVo;
import com.kingcent.afast.vo.ProjectServiceVo;
import com.kingcent.common.entity.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
@Service
public class ProjectServiceServiceImpl extends ServiceImpl<ProjectServiceMapper, ProjectServiceEntity> implements ProjectServiceService {

    @Autowired
    private ProjectEntityService projectEntityService;

    @Autowired
    private ProjectServiceFuncService projectServiceFuncService;

    @Autowired
    private ProjectDaoService projectDaoService;

    @Override
    public Result<Page<ProjectServiceVo>> list(Long userId, Long projectId, Long pageSize, Long pageNum) {
        LambdaQueryWrapper<ProjectServiceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectServiceEntity::getProjectId, projectId);
        Page<ProjectServiceEntity> page = page(new Page<>(pageNum, pageSize), wrapper);
        Page<ProjectServiceVo> resPage = new Page<>();
        resPage.setTotal(page.getTotal());
        resPage.setCurrent(page.getCurrent());
        resPage.setSize(page.getSize());
        List<ProjectServiceVo> records = new ArrayList<>();
        resPage.setRecords(records);
        Set<Long> entityIds = new HashSet<>();
        Set<Long> daoIds = new HashSet<>();
        for (ProjectServiceEntity record : page.getRecords()) {
            entityIds.add(record.getEntityId());
            Long daoId = record.getDaoId();
            if(daoId != null){
                daoIds.add(daoId);
            }
        }
        Map<Long,String> entityNames = projectEntityService.getNameMap(entityIds);
        Map<Long,String> daoNames = projectDaoService.getNameMap(daoIds);

        for (ProjectServiceEntity record : page.getRecords()) {
            ProjectServiceVo vo = new ProjectServiceVo();
            vo.setId(record.getId());
            vo.setEntityId(record.getEntityId());
            vo.setName(record.getName());
            vo.setCreateTime(record.getCreateTime());
            vo.setCountMethod(0);
            vo.setDaoName(daoNames.getOrDefault(record.getDaoId(),""));
            vo.setEntityName(entityNames.getOrDefault(record.getEntityId(),""));
            vo.setDescription(record.getDescription());
            vo.setProjectId(record.getProjectId());
            records.add(vo);
        }
        return Result.success(resPage);
    }

    @Override
    public Result<?> save(Long userId, Long projectId, ProjectServiceDto serviceDto) {
        ProjectServiceEntity service = new ProjectServiceEntity();
        ProjectEntityEntity entityEntity = projectEntityService.get(userId, projectId, serviceDto.getEntityId());
        if(entityEntity == null){
            return Result.fail("绑定的实体不存在");
        }
        service.setProjectId(projectId);
        service.setId(serviceDto.getId());
        service.setDaoId(serviceDto.getDaoId());
        service.setName(serviceDto.getName());
        service.setDescription(serviceDto.getDescription());
        service.setCreateTime(LocalDateTime.now());
        service.setEntityId(serviceDto.getEntityId());
        saveOrUpdate(service);
        return Result.success("保存成功");
    }

    @Override
    public Result<?> delete(Long userId, Long projectId, Long serviceId) {
        LambdaQueryWrapper<ProjectServiceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectServiceEntity::getProjectId,projectId);
        wrapper.eq(ProjectServiceEntity::getId,serviceId);
        remove(wrapper);
        return Result.success("删除成功");
    }

    @Override
    public Result<?> generateJava(Long userId, Long projectId, Long serviceId) {
        LambdaQueryWrapper<ProjectServiceEntity> w1 = new LambdaQueryWrapper<>();
        w1.eq(ProjectServiceEntity::getProjectId,projectId);
        w1.eq(ProjectServiceEntity::getId, serviceId);
        ProjectServiceEntity service = getOne(w1);
        if(service == null){
            return Result.fail("服务不存在");
        }


        LambdaQueryWrapper<ProjectServiceFuncEntity> w2 = new LambdaQueryWrapper<>();
        w2.eq(ProjectServiceFuncEntity::getProjectId,projectId);
        w2.eq(ProjectServiceFuncEntity::getServiceId, serviceId);
        List<ProjectServiceFuncEntity> list = projectServiceFuncService.list(w2);

        ProjectEntityEntity entity = projectEntityService.get(userId, projectId, service.getEntityId());
        if (entity == null){
            return Result.fail("实体不存在");
        }

        //有绑定dao时，获取对应的dao
        ProjectDaoEntity dao = null;
        if(service.getDaoId() != null) {
            LambdaQueryWrapper<ProjectDaoEntity> w3 = new LambdaQueryWrapper<>();
            w3.eq(ProjectDaoEntity::getProjectId, projectId);
            w3.eq(ProjectDaoEntity::getId, service.getDaoId());
            dao = projectDaoService.getOne(w3);
        }

        return Result.success("操作成功",
                ProjectServiceUtil.generateJava(
                        "com.a.a",
                        entity,
                        dao,
                        service,
                        list,
                        true,
                        true,
                        13
                )
        );
    }
}