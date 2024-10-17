package com.kingcent.afast.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.dto.ProjectEntityDto;
import com.kingcent.afast.entity.ProjectEntity;
import com.kingcent.afast.entity.ProjectEntityEntity;
import com.kingcent.afast.mapper.ProjectEntityMapper;
import com.kingcent.afast.mapper.ProjectMapper;
import com.kingcent.afast.service.ProjectEntityService;
import com.kingcent.afast.service.ProjectService;
import com.kingcent.afast.utils.ProjectEntityUtil;
import com.kingcent.common.entity.result.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
@Service
public class ProjectEntityServiceImpl extends ServiceImpl<ProjectEntityMapper, ProjectEntityEntity> implements ProjectEntityService {

    @Override
    public Result<Page<ProjectEntityEntity>> list(Long userId,Long projectId, Long pageSize, Long pageNum) {
        LambdaQueryWrapper<ProjectEntityEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectEntityEntity::getProjectId, projectId);
        return Result.success(page(new Page<>(pageNum,pageSize),wrapper));
    }

    @Override
    public Result<?> save(Long userId, Long projectId, ProjectEntityDto entity) {
        ProjectEntityEntity projectEntity = new ProjectEntityEntity();
        projectEntity.setProjectId(projectId);
        projectEntity.setId(entity.getId());
        projectEntity.setName(entity.getName());
        projectEntity.setValue(entity.getValue());
        projectEntity.setDescription(entity.getDescription());
        projectEntity.setTableName(entity.getTableName());
        projectEntity.setCreateTime(LocalDateTime.now());
        saveOrUpdate(projectEntity);
        return Result.success("保存成功");
    }

    @Override
    public Result<?> delete(Long userId, Long projectId, Long entityId) {
        LambdaQueryWrapper<ProjectEntityEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectEntityEntity::getProjectId,projectId);
        wrapper.eq(ProjectEntityEntity::getId,entityId);
        remove(wrapper);
        return Result.success("删除成功");
    }

    //获取字段类型对应sql的类型
    public static String parseSqlType(String type){
        return switch (type) {
            case "int" -> "INT";
            case "long" -> "BIGINT";
            case "string" -> "VARCHAR(255)";
            case "longtext" -> "LONGTEXT";
            case "datetime" -> "DATETIME";
            case "date" -> "DATE";
            case "decimal" -> "DECIMAL";
            case "boolean" -> "TINYINT";
            default -> "VARCHAR";
        };
    }

    @Override
    public Result<String> toSql(Long userId, Long projectId, Long entityId) {
        LambdaQueryWrapper<ProjectEntityEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectEntityEntity::getProjectId,projectId);
        wrapper.eq(ProjectEntityEntity::getId,entityId);
        ProjectEntityEntity projectEntity = getOne(wrapper);
        List<String> fields = new ArrayList<>();
        String value = projectEntity.getValue();
        try{
            List<Map<String,Object>> data = JSONObject.parseObject(value,List.class);
            for (Map<String, Object> field : data) {
                String name = (String) field.get("name");
                String type = (String) field.get("type");
                Boolean isTableField = (Boolean) field.getOrDefault("isTableField", false);
                if(name == null || type == null){
                    return Result.fail("字段内容错误");
                }
                if(isTableField){
                    fields.add("\t`"+ StringUtils.camelToUnderline(name) +"`, "+parseSqlType(type)+" #"+field.getOrDefault("description",""));
                }
            }
        }catch (Exception e){
            return Result.fail("实体内容错误");
        }
        return Result.success(
                "操作成功",
                "/*\n"+projectEntity.getDescription()+"\n*/\nCREATE TABLE `"+projectEntity.getTableName()+"`(\n" +
                        String.join("\n",fields)+
                "\n)"
        );
    }

    @Override
    public ProjectEntityEntity get(Long userId, Long projectId, Long entityId) {
        LambdaQueryWrapper<ProjectEntityEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectEntityEntity::getProjectId,projectId);
        wrapper.eq(ProjectEntityEntity::getId,entityId);
        return getOne(wrapper);
    }

    @Override
    public Map<Long, String> getNameMap(Set<Long> entityIds) {
        Map<Long,String> names = new HashMap<>();
        if (entityIds.size() == 0) return names;
        LambdaQueryWrapper<ProjectEntityEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ProjectEntityEntity::getId,entityIds);
        wrapper.select(ProjectEntityEntity::getName,ProjectEntityEntity::getId);
        List<ProjectEntityEntity> list = list(wrapper);
        for (ProjectEntityEntity entityEntity : list) {
            names.put(entityEntity.getId(),entityEntity.getName());
        }
        return names;
    }

    @Override
    public Result<String> toJava(Long userId, Long projectId, Long entityId) {
        ProjectEntityEntity entityEntity = get(userId, projectId, entityId);
        if(entityEntity == null) return Result.fail("实体不存在");
        String code = ProjectEntityUtil.generate("com.a",entityEntity,true, true);
        return Result.success("成功",code);
    }
}