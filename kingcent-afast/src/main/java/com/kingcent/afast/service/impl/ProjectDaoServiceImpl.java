package com.kingcent.afast.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.dto.ProjectDaoDto;
import com.kingcent.afast.dto.ProjectEntityDto;
import com.kingcent.afast.entity.ProjectDaoEntity;
import com.kingcent.afast.entity.ProjectEntityEntity;
import com.kingcent.afast.mapper.ProjectDaoMapper;
import com.kingcent.afast.mapper.ProjectEntityMapper;
import com.kingcent.afast.service.ProjectDaoService;
import com.kingcent.afast.service.ProjectEntityService;
import com.kingcent.afast.vo.ProjectDaoVo;
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
public class ProjectDaoServiceImpl extends ServiceImpl<ProjectDaoMapper, ProjectDaoEntity> implements ProjectDaoService {

    @Autowired
    private ProjectEntityService projectEntityService;

    @Override
    public Result<Page<ProjectDaoVo>> list(Long userId, Long projectId, Long pageSize, Long pageNum) {
        LambdaQueryWrapper<ProjectDaoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectDaoEntity::getProjectId, projectId);
        Page<ProjectDaoEntity> page = page(new Page<>(pageNum, pageSize), wrapper);
        Page<ProjectDaoVo> resPage = new Page<>();
        resPage.setTotal(page.getTotal());
        resPage.setCurrent(page.getCurrent());
        resPage.setSize(page.getSize());
        List<ProjectDaoVo> records = new ArrayList<>();
        resPage.setRecords(records);
        Set<Long> entityIds = new HashSet<>();
        for (ProjectDaoEntity record : page.getRecords()) {
            entityIds.add(record.getEntityId());
        }
        Map<Long,String> names = projectEntityService.getNameMap(entityIds);
        for (ProjectDaoEntity record : page.getRecords()) {
            ProjectDaoVo vo = new ProjectDaoVo();
            vo.setId(record.getId());
            vo.setEntityId(record.getEntityId());
            vo.setName(record.getName());
            vo.setCreateTime(record.getCreateTime());
            vo.setCountMethod(0);
            vo.setEntityName(names.getOrDefault(record.getEntityId(),""));
            vo.setDescription(record.getDescription());
            vo.setProjectId(record.getProjectId());
            records.add(vo);
        }
        return Result.success(resPage);
    }

    @Override
    public Result<?> save(Long userId, Long projectId, ProjectDaoDto daoDto) {
        ProjectDaoEntity dao = new ProjectDaoEntity();
        ProjectEntityEntity entityEntity = projectEntityService.get(userId, projectId, daoDto.getEntityId());
        if(entityEntity == null){
            return Result.fail("绑定的实体不存在");
        }
        dao.setProjectId(projectId);
        dao.setId(daoDto.getId());
        dao.setName(daoDto.getName());
        dao.setDescription(daoDto.getDescription());
        dao.setCreateTime(LocalDateTime.now());
        dao.setEntityId(daoDto.getEntityId());
        saveOrUpdate(dao);
        return Result.success("保存成功");
    }

    @Override
    public Result<?> delete(Long userId, Long projectId, Long daoId) {
        LambdaQueryWrapper<ProjectDaoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectDaoEntity::getProjectId,projectId);
        wrapper.eq(ProjectDaoEntity::getId,daoId);
        remove(wrapper);
        return Result.success("删除成功");
    }
}