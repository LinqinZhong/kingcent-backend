package com.kingcent.afast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.entity.*;
import com.kingcent.afast.mapper.ProjectMapper;
import com.kingcent.afast.object.JavaDependency;
import com.kingcent.afast.service.*;
import com.kingcent.afast.utils.ProjectServiceUtil;
import com.kingcent.afast.utils.ProjectUtil;
import com.kingcent.common.entity.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2024/10/13 15:15
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, ProjectEntity> implements ProjectService {


    @Autowired
    private ProjectEntityService projectEntityService;

    @Autowired
    private ProjectServiceService projectServiceService;

    @Autowired
    private ProjectDaoFuncService projectDaoFuncService;

    @Autowired
    private ProjectDaoService projectDaoService;

    @Autowired
    private ProjectServiceFuncService projectServiceFuncService;

    @Autowired
    private ProjectMvnDepService projectMvnDepService;

    @Override
    public void create(Long userId, Long groupId, String name) {
        ProjectEntity project = new ProjectEntity();
        project.setName(name);
        save(project);
    }

    @Override
    public Result<Page<ProjectEntity>> list(Long userId,Long groupId, Long pageSize, Long pageNum) {
        LambdaQueryWrapper<ProjectEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectEntity::getGroupId, groupId);
        return Result.success("",page(new Page<>(pageNum,pageSize),wrapper));
    }

    @Override
    public Result<?> build(Long userId, Long projectId) throws IOException, ProjectUtil.BuildProjectException {

        LambdaQueryWrapper<ProjectEntity> w1 = new LambdaQueryWrapper<>();
        w1.eq(ProjectEntity::getId, projectId);
        ProjectEntity project = getOne(w1);

        //实体字典
        Map<Long, ProjectEntityEntity> entityMap = new HashMap<>();
        //dao字典
        Map<Long, ProjectDaoEntity> daoMap = new HashMap<>();
        //项目目录
        File projectDir = ProjectUtil.buildProjectDir(project);
        //src目录
        File srcDir = ProjectUtil.buildSrcDir(projectDir);
        //包目录
        File packageDir = ProjectUtil.buildPackageDir(srcDir,project.getPackageName());
        //Afast代码存放目录
        File afastDir = ProjectUtil.buildCoreDir(packageDir);
        //实体目录
        File entityDir = ProjectUtil.buildEntityDir(afastDir);
        //实体目录
        File daoDir = ProjectUtil.buildDaoDir(afastDir);
        //实体目录
        File serviceDir = ProjectUtil.buildServiceDir(afastDir);
        //依赖列表
        List<ProjectMvnDepEntity> dependencyList = projectMvnDepService.getProjectDependencies(projectId, true);
        //构建POM文件
        ProjectUtil.buildPom(srcDir, project,dependencyList);
        //构建Application
        ProjectUtil.buildApplication(packageDir, project);

        //构建实体类
        LambdaQueryWrapper<ProjectEntityEntity> w2 = new LambdaQueryWrapper<>();
        w2.eq(ProjectEntityEntity::getProjectId,projectId);
        List<ProjectEntityEntity> entityList = projectEntityService.list(w2);
        //记录全部实体类
        for (ProjectEntityEntity entityEntity : entityList) {
            entityMap.put(entityEntity.getId(), entityEntity);
        }
        //构建实体类文件
        ProjectUtil.buildEntityFiles(entityDir,project.getPackageName(),entityList);

        //构建数据层
        LambdaQueryWrapper<ProjectDaoEntity> w3 = new LambdaQueryWrapper<>();
        w3.eq(ProjectDaoEntity::getProjectId, projectId);
        List<ProjectDaoEntity> daoList = projectDaoService.list(w3);
        //获取对应方法
        List<Long> daoIds = new ArrayList<>();
        Map<Long,List<ProjectDaoFuncEntity>> daoFuncMap = new HashMap<>();
        for (ProjectDaoEntity daoEntity : daoList) {
            daoIds.add(daoEntity.getId());
            daoMap.put(daoEntity.getId(), daoEntity);
        }
        if(daoIds.size() > 0) {
            LambdaQueryWrapper<ProjectDaoFuncEntity> w4 = new LambdaQueryWrapper<>();
            w4.in(ProjectDaoFuncEntity::getDaoId,daoIds);
            List<ProjectDaoFuncEntity> daoFuncList = projectDaoFuncService.list(w4);
            for (ProjectDaoFuncEntity daoFunc : daoFuncList) {
                if(!daoFuncMap.containsKey(daoFunc.getDaoId())){
                    daoFuncMap.put(daoFunc.getDaoId(),new ArrayList<>());
                }
                daoFuncMap.get(daoFunc.getDaoId()).add(daoFunc);
            }
        }
        ProjectUtil.buildDaoFiles(
                daoDir,
                project.getPackageName(),
                daoList,
                entityMap,
                daoFuncMap
        );

        //构建服务层
        LambdaQueryWrapper<ProjectServiceEntity> w5 = new LambdaQueryWrapper<>();
        w5.eq(ProjectServiceEntity::getProjectId,projectId);
        List<ProjectServiceEntity> serviceList = projectServiceService.list(w5);
        //获取对应方法
        List<Long> serviceIds = new ArrayList<>();
        Map<Long,List<ProjectServiceFuncEntity>> serviceFuncMap = new HashMap<>();
        for (ProjectServiceEntity serviceEntity : serviceList) {
            serviceIds.add(serviceEntity.getId());
        }
        if(serviceIds.size() > 0) {
            LambdaQueryWrapper<ProjectServiceFuncEntity> w6 = new LambdaQueryWrapper<>();
            w6.in(ProjectServiceFuncEntity::getServiceId,serviceIds);
            List<ProjectServiceFuncEntity> serviceFuncList = projectServiceFuncService.list(w6);
            for (ProjectServiceFuncEntity func : serviceFuncList) {
                if(!serviceFuncMap.containsKey(func.getServiceId())){
                    serviceFuncMap.put(func.getServiceId(),new ArrayList<>());
                }
                serviceFuncMap.get(func.getServiceId()).add(func);
            }
        }
        ProjectUtil.buildServiceFiles(
                serviceDir,
                project.getPackageName(),
                serviceList,
                entityMap,
                daoMap,
                serviceFuncMap
        );

        return Result.success();
    }
}
