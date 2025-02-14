package com.kingcent.afast.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kingcent.afast.entity.ProjectServiceFuncEntity;
import com.kingcent.afast.entity.ProjectServiceImplEntity;
import com.kingcent.afast.entity.ProjectServiceImplFuncEntity;
import com.kingcent.afast.mapper.ProjectServiceFuncMapper;
import com.kingcent.afast.service.ProjectServiceFuncService;
import com.kingcent.afast.service.ProjectServiceImplFuncService;
import com.kingcent.afast.service.ProjectServiceImplService;
import com.kingcent.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author rainkyzhong
 * @date 2024/10/17 17:48
 */
@Service
public class ProjectServiceFuncServiceImpl extends ServiceImpl<ProjectServiceFuncMapper, ProjectServiceFuncEntity> implements ProjectServiceFuncService {
    @Autowired
    @Lazy
    private ProjectServiceImplService serviceImplService;
    @Autowired
    @Lazy
    private ProjectServiceImplFuncService serviceImplFuncService;

    @Override
    public Result<Page<ProjectServiceFuncEntity>> list(Long userId, Long projectId, Long serviceId, Long pageNum, Long pageSize) {
        LambdaQueryWrapper<ProjectServiceFuncEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectServiceFuncEntity::getProjectId, projectId);
        wrapper.eq(ProjectServiceFuncEntity::getServiceId, serviceId);
        return Result.success(page(new Page<>(pageNum,pageSize),wrapper));
    }

    @Override
    @Transactional
    public Result<?> save(Long userId, Long projectId,Long serviceId, ProjectServiceFuncEntity func) {

        func.setProjectId(projectId);
        func.setServiceId(serviceId);
        func.setCreateTime(LocalDateTime.now());

        //处理传过来的params字段
        String params = func.getParams();
        if(params == null) params = "[]";
        else{
            try{
                List<Map<String,Object>> res = new ArrayList<>();
                List<JSONObject> list = JSON.parseArray(params, JSONObject.class);
                for (JSONObject jsonObject : list) {
                    if (jsonObject != null){
                        String name = jsonObject.getString("name");
                        String type = jsonObject.getString("type");
                        String description = (String) jsonObject.getOrDefault("description","");
                        res.add(
                                Map.of(
                                        "name",name,
                                        "type",type,
                                        "description",description
                                )
                        );
                    }
                }
                params = JSON.toJSONString(res);
            }catch (Exception e){
                return Result.fail("参数错误");
            }
        }
        func.setParams(params);


        //检测是否存在重复的方法
        Long id = baseMapper.signExist(
                projectId,
                serviceId,
                func.getParams(),
                func.getReturnParam(),
                func.getName()
        );
        if(id != null && !id.equals(func.getId())){
            return Result.fail("已存在同样的方法");
        }
        saveOrUpdate(func);

        //将改动同步到实现类中
        LambdaQueryWrapper<ProjectServiceImplEntity> w1 = new LambdaQueryWrapper<>();
        w1.eq(ProjectServiceImplEntity::getServiceId,serviceId);
        w1.select(ProjectServiceImplEntity::getId);
        List<Long> implIds = serviceImplService.list(w1).stream().map(ProjectServiceImplEntity::getId).toList();
        if(implIds.size() > 0) {
                /*
                    修改实现类的方法，要保证修改之后，不会与原来已经存在的方法冲突
                    如原来的实现类中已经存在方法int get(int id)
                    此时我们在接口创建int get(int id)方法并同步到实现类中，再创建一个int get(int id)方法会导致方法重复
                    此时只需要沿用已存在的方法即可
                    当然要注意，如果是private，需要将其修改为public
                 */
            //发生冲突的实现类
            List<Long> collisionImplIds = new ArrayList<>();
            List<Long> collisionImplFuncIds = new ArrayList<>();
            //获取签名冲突的方法
            LambdaQueryWrapper<ProjectServiceImplFuncEntity> w2 = new LambdaQueryWrapper<>();
            w2.eq(ProjectServiceImplFuncEntity::getServiceId, serviceId);
            w2.in(ProjectServiceImplFuncEntity::getImplId, implIds);
            w2.eq(ProjectServiceImplFuncEntity::getName, func.getName());
            w2.eq(ProjectServiceImplFuncEntity::getParams, func.getParams());
            w2.eq(ProjectServiceImplFuncEntity::getFuncId, -1L);
            w2.eq(ProjectServiceImplFuncEntity::getReturnParam, func.getReturnParam());
            w2.eq(ProjectServiceImplFuncEntity::getType, 0);
            List<ProjectServiceImplFuncEntity> list = serviceImplFuncService.list(w2);
            for (ProjectServiceImplFuncEntity implFunc : list) {
                //记录冲突的实现类
                collisionImplIds.add(implFunc.getImplId());
                collisionImplFuncIds.add(implFunc.getId());
            }

            //修改冲突的实现类，沿用方法
            if(collisionImplIds.size() > 0) {
                //去除原来方法的Override
                ProjectServiceImplFuncEntity implFunc1 = new ProjectServiceImplFuncEntity();
                implFunc1.setFuncId(-1L);
                //更新
                LambdaQueryWrapper<ProjectServiceImplFuncEntity> w4 = new LambdaQueryWrapper<>();
                w4.eq(ProjectServiceImplFuncEntity::getServiceId, serviceId);
                w4.in(ProjectServiceImplFuncEntity::getImplId, collisionImplIds);
                w4.in(ProjectServiceImplFuncEntity::getFuncId, func.getId());
                serviceImplFuncService.update(implFunc1, w4);

                //修改已存在的方法
                ProjectServiceImplFuncEntity implFunc0 = new ProjectServiceImplFuncEntity();
                LambdaQueryWrapper<ProjectServiceImplFuncEntity> w3 = new LambdaQueryWrapper<>();
                w3.eq(ProjectServiceImplFuncEntity::getServiceId, serviceId);
                w3.in(ProjectServiceImplFuncEntity::getFuncId, collisionImplFuncIds);
                //将方法设置为public
                implFunc0.setScope(0);
                //设置funcId
                implFunc0.setFuncId(func.getId());
                //更新
                serviceImplFuncService.update(implFunc0, w2);
            }

            //获取未冲突的id
            List<Long> noCollisionImplIds = implIds.stream().filter(a->!collisionImplIds.contains(a)).toList();

            if(noCollisionImplIds.size() > 0){
                LambdaQueryWrapper<ProjectServiceImplFuncEntity> w5 = new LambdaQueryWrapper<>();
                w5.eq(ProjectServiceImplFuncEntity::getProjectId, projectId);
                w5.eq(ProjectServiceImplFuncEntity::getServiceId, serviceId);
                w5.eq(ProjectServiceImplFuncEntity::getFuncId, func.getId());
                w5.in(ProjectServiceImplFuncEntity::getImplId,noCollisionImplIds);
                //获取存在的方法
                List<ProjectServiceImplFuncEntity> exitsList = serviceImplFuncService.list(w5);
                Set<Long> exitsIds = new HashSet<>();
                for (ProjectServiceImplFuncEntity implFunc : exitsList) {
                    implFunc.setName(func.getName());
                    implFunc.setReturnParam(func.getReturnParam());
                    implFunc.setParams(func.getParams());
                    exitsIds.add(implFunc.getImplId());
                }
                serviceImplFuncService.updateBatchById(exitsList);

                //创建
                for (Long implId : noCollisionImplIds) {
                    if(exitsIds.contains(implId)) continue;
                    ProjectServiceImplFuncEntity implFunc2 = new ProjectServiceImplFuncEntity();
                    implFunc2.setName(func.getName());
                    implFunc2.setReturnParam(func.getReturnParam());
                    implFunc2.setParams(func.getParams());
                    implFunc2.setType(0);
                    implFunc2.setScope(0);
                    implFunc2.setFuncId(func.getId());
                    implFunc2.setEntityId(func.getEntityId());
                    implFunc2.setProjectId(func.getProjectId());
                    implFunc2.setServiceId(func.getServiceId());
                    implFunc2.setImplId(implId);
                    implFunc2.setCreateTime(LocalDateTime.now());
                    serviceImplFuncService.save(implFunc2);
                }
            }
        }

        return Result.success();
    }

    @Override
    public Result<?> delete(Long userId, Long projectId, Long serviceId, Long funcId) {
        LambdaQueryWrapper<ProjectServiceFuncEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectServiceFuncEntity::getProjectId,projectId);
        wrapper.eq(ProjectServiceFuncEntity::getServiceId,serviceId);
        wrapper.eq(ProjectServiceFuncEntity::getId,funcId);
        remove(wrapper);
        return Result.success("删除成功");
    }
}
